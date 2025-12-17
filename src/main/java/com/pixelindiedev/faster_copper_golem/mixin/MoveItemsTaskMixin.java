package com.pixelindiedev.faster_copper_golem.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.ai.brain.task.MoveItemsTask;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.block.entity.BlockEntity;

// MODIFIED: Imports for Frame/Tag sorting
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.item.Item;
import java.util.List;
import java.util.Optional;

// MODIFIED: Import for name checking
import net.minecraft.util.Nameable;

import static com.pixelindiedev.faster_copper_golem.Faster_copper_golem.*;

@Mixin(value = MoveItemsTask.class, priority = 800)
public abstract class MoveItemsTaskMixin {
    @Mutable
    @Final
    @Shadow
    private int horizontalRange;
    @Mutable
    @Final
    @Shadow
    private int verticalRange;
    @Mutable
    @Final
    @Shadow
    private float speed;

    // MODIFIED: Shadow outputChestPredicate for smart sorting logic
    @Mutable
    @Shadow
    private Predicate outputChestPredicate;

    // MODIFIED: Capture owner for smart sorting
    @Unique
    private LivingEntity faster_copper_golem$capturedOwner;

    // MODIFIED: Capture owner in shouldRun
    @Inject(method = "shouldRun", at = @At("HEAD"))
    private void captureOwner(ServerWorld world, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        this.faster_copper_golem$capturedOwner = entity;
    }

    @ModifyExpressionValue(method = "extractStack", at = @At(value = "CONSTANT", args = "intValue=16"))
    private static int increaseStackAmount(int original) {
        return getMaxStackSize();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void captureData(float speed, Predicate inputContainerPredicate, Predicate outputChestPredicate,
            int horizontalRange, int verticalRange, Map interactionCallbacks, Consumer travellingCallback,
            Predicate storagePredicate, CallbackInfo ci) {
        this.horizontalRange = getHorizontalSearchRadius();
        this.verticalRange = getVerticalSearchRadius();
        this.speed = getMovementSpeed();

        // MODIFIED: Smart sorting predicate wrapper with Tags and Frames
        if (CONFIG.smartSorting) {
            Predicate original = this.outputChestPredicate;
            this.outputChestPredicate = (obj) -> {
                if (!original.test(obj))
                    return false;
                if (this.faster_copper_golem$capturedOwner == null)
                    return true;

                LivingEntity golem = this.faster_copper_golem$capturedOwner;
                ItemStack held = golem.getMainHandStack();
                if (held.isEmpty())
                    return true;

                if (obj instanceof BlockEntity) {
                    BlockEntity be = (BlockEntity) obj; // Inventory often implements BlockEntity (ChestBlockEntity etc)

                    // 0. Name Logic (!Dump / !Ignore)
                    if (CONFIG.nameSorting && be instanceof Nameable) {
                        Nameable nameable = (Nameable) be;
                        if (nameable.hasCustomName()) {
                            String name = nameable.getCustomName().getString();
                            if (name.equals("!Dump"))
                                return true; // Accept EVERYTHING
                            if (name.equals("!Ignore"))
                                return false; // Accept NOTHING (Strict)
                        }
                    }

                    // 1. Item Frame Logic
                    if (CONFIG.frameSorting) {
                        ItemStack frameStack = findItemFrameItem(be);
                        if (frameStack != null && !frameStack.isEmpty()) {
                            // Strict Filter: If frame exists, MUST match frame
                            return matches(held, frameStack);
                        }
                    }

                    // 2. Existing Inventory Logic (Smart Sorting / Tag Sorting)
                    if (obj instanceof Inventory) {
                        Inventory inv = (Inventory) obj;
                        if (inv.isEmpty())
                            return true; // Always allow empty if no frame restriction

                        // Check for ANY match in existing slots
                        for (int i = 0; i < inv.size(); i++) {
                            ItemStack s = inv.getStack(i);
                            if (!s.isEmpty() && matches(held, s))
                                return true;
                        }

                        return false; // No match found in non-empty chest -> Reject
                    }
                }
                return true;
            };
        }

        AddTask((MoveItemsTask) (Object) this);
    }

    // MODIFIED: Helper - Check if two stacks match (Exact or Tag)
    @Unique
    private boolean matches(ItemStack stack1, ItemStack stack2) {
        if (ItemStack.areItemsEqual(stack1, stack2))
            return true; // Exact match

        if (CONFIG.tagSorting) {
            // Check if they share ANY common tag
            // Note: This iterates all tags. Might be broad.
            // A better heuristic: Do they share a tag that is NOT "minecraft:items" etc?
            // For simplicity in this mod: equality of ANY tag.
            // MODIFIED: Use stream processing
            return stack1.streamTags().anyMatch(tag -> stack2.streamTags().anyMatch(t -> t.equals(tag)));
        }
        return false;
    }

    // MODIFIED: Helper - Find Item Frame attached to BlockEntity
    @Unique
    private ItemStack findItemFrameItem(BlockEntity be) {
        if (be.getWorld() == null)
            return null;
        BlockPos pos = be.getPos();

        // Search for Item Frames in a 1-block radius box
        List<ItemFrameEntity> frames = be.getWorld().getEntitiesByClass(
                ItemFrameEntity.class,
                new Box(pos).expand(1.0),
                frame -> {
                    BlockPos attached = frame.getAttachedBlockPos(); // Mappings might vary: getAttachedPos,
                                                                     // getDecorationBlockPos?
                    // Fallback logic if mapping is obscure: check distance or direction
                    return attached.equals(pos);
                });

        if (!frames.isEmpty()) {
            return frames.get(0).getHeldItemStack();
        }
        return null;
    }

    @ModifyExpressionValue(method = "tickInteracting", at = @At(value = "CONSTANT", args = "intValue=60"))
    private int reduceInteractionTime(int original) {
        return getInteractionTime(original);
    }

    @ModifyExpressionValue(method = "cooldown", at = @At(value = "CONSTANT", args = "intValue=140"))
    private int reduceCooldown(int original) {
        return getCooldownTime(original);
    }

    @ModifyExpressionValue(method = "markVisited", at = @At(value = "CONSTANT", args = "intValue=10"))
    private int increaseVisitedChestMemory(int original) {
        return getMaxChestsRemembered(original);
    }
}
