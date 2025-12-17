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

// MODIFIED: Added imports for smart sorting
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.block.entity.BlockEntity;

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

        // MODIFIED: Smart sorting predicate wrapper
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

                if (obj instanceof Inventory) {
                    Inventory inv = (Inventory) obj;
                    // Anti-mixing: Allow if empty OR contains matching item
                    if (inv.isEmpty())
                        return true;

                    for (int i = 0; i < inv.size(); i++) {
                        ItemStack s = inv.getStack(i);
                        if (ItemStack.areItemsEqual(s, held))
                            return true;
                    }

                    return false; // Mixed content -> Reject
                }
                return true;
            };
        }

        AddTask((MoveItemsTask) (Object) this);
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
