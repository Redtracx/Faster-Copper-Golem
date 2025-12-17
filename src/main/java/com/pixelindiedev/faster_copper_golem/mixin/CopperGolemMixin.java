package com.pixelindiedev.faster_copper_golem.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.pixelindiedev.faster_copper_golem.config.ClientConfigCache;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CopperGolemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// MODIFIED: Imports for GUI/Inventory
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import com.pixelindiedev.faster_copper_golem.screen.GolemFilterScreenHandler;

@Mixin(value = CopperGolemEntity.class, priority = 800)
public abstract class CopperGolemMixin implements CopperGolemAccessor, NamedScreenHandlerFactory {
    // MODIFIED: Memory and Filter Storage
    @Unique
    private final java.util.Map<net.minecraft.item.Item, net.minecraft.util.math.BlockPos> faster_copper_golem$chestMemory = new java.util.HashMap<>();

    // MODIFIED: Inventory for filter (9 slots)
    @Unique
    private final SimpleInventory faster_copper_golem$filterInventory = new SimpleInventory(9);

    // MODIFIED: NBT keys for persistence
    @Unique
    private static final String NBT_FILTER_INVENTORY = "faster_copper_golem$FilterInventory";
    @Unique
    private static final String NBT_CHEST_MEMORY = "faster_copper_golem$ChestMemory";

    // NamedScreenHandlerFactory implementation
    @Override
    public Text getDisplayName() {
        return Text.of("Copper Golem Filter");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GolemFilterScreenHandler(syncId, playerInventory, faster_copper_golem$filterInventory);
    }

    @Unique
    @Override
    public SimpleInventory faster_copper_golem$getFilterInventory() {
        return faster_copper_golem$filterInventory;
    }

    // Unused method in interface now, but keeping implementation empty or removing
    // if interface updated (interface WAS updated).
    // The previous interface methods toggleFilter etc were removed in last step.

    @Unique
    public boolean faster_copper_golem$isItemAllowed(net.minecraft.item.Item item) {
        // Blacklist logic: If item matches anything in inventory -> BLOCKED.
        for (int i = 0; i < faster_copper_golem$filterInventory.size(); i++) {
            net.minecraft.item.ItemStack s = faster_copper_golem$filterInventory.getStack(i);
            if (!s.isEmpty() && s.getItem() == item)
                return false; // Found in blacklist -> Blocked
        }
        return true;
    }

    @Unique
    @Override
    public void faster_copper_golem$rememberChest(net.minecraft.item.Item item, net.minecraft.util.math.BlockPos pos) {
        faster_copper_golem$chestMemory.put(item, pos);
    }

    @Unique
    @Override
    public net.minecraft.util.math.BlockPos faster_copper_golem$getRememberedChest(net.minecraft.item.Item item) {
        return faster_copper_golem$chestMemory.get(item);
    }

    @Unique
    @Override
    public java.util.Map<net.minecraft.item.Item, net.minecraft.util.math.BlockPos> faster_copper_golem$getChestMemory() {
        return faster_copper_golem$chestMemory;
    }

    // MODIFIED: Interaction to open Filter GUI (Shift-Right-Click)
    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(net.minecraft.entity.player.PlayerEntity player, net.minecraft.util.Hand hand,
            CallbackInfoReturnable<net.minecraft.util.ActionResult> cir) {
        if (player.isSneaking() && hand == net.minecraft.util.Hand.MAIN_HAND) {
            // Open GUI
            player.openHandledScreen(this);
            cir.setReturnValue(net.minecraft.util.ActionResult.SUCCESS);
        }
    }

    // MODIFIED: Prevent pickup if filtered
    // Commented out due to mapping issues ("canGather" not found).
    // Logic will only apply to inventory operations for now.
    /*
     * @Inject(method = "canGather", at = @At("HEAD"), cancellable = true)
     * private void checkFilter(net.minecraft.item.ItemStack stack,
     * CallbackInfoReturnable<Boolean> cir) {
     * if (!faster_copper_golem$isItemAllowed(stack.getItem())) {
     * cir.setReturnValue(false);
     * }
     * }
     */

    @Unique
    private CopperGolemEntity self;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void captureData(EntityType entityType, World world, CallbackInfo ci) {
        self = (CopperGolemEntity) (Object) this;
    }

    @Inject(method = "clientTick", at = @At("HEAD"))
    private void accelerateAnimations(CallbackInfo ci) {
        int speed = (int) (1 / ClientConfigCache.speedMultiplier);

        accelerate(self.getSpinHeadAnimationState(), speed);
        accelerate(self.getGettingItemAnimationState(), speed);
        accelerate(self.getGettingNoItemAnimationState(), speed);
        accelerate(self.getDroppingItemAnimationState(), speed);
        accelerate(self.getDroppingNoItemAnimationState(), speed);
    }

    @ModifyExpressionValue(method = "clientTick", at = @At(value = "CONSTANT", args = "floatValue=10.0F"))
    private float editSpinHeadTimerPlus(float original) {
        return original * ClientConfigCache.speedMultiplier;
    }

    @ModifyExpressionValue(method = "clientTick", at = @At(value = "CONSTANT", args = "intValue=200"))
    private int editRandomNext(int original) {
        return (int) (original * ClientConfigCache.speedMultiplier);
    }

    @ModifyExpressionValue(method = "clientTick", at = @At(value = "CONSTANT", args = "intValue=240"))
    private int editRandomNext2(int original) {
        return (int) (original * ClientConfigCache.speedMultiplier);
    }

    // MODIFIED: Fixed issue #1
    // Reason: Animations were artificially stopped at high speeds (>4) instead of
    // being accelerated.
    @Unique
    private void accelerate(AnimationState state, int speed) {
        if (state.isRunning()) {
            state.skip(1, speed - 1);
        }
    }

    // MODIFIED: NBT Persistence - Save filter inventory and chest memory
    @Inject(method = "writeNbt", at = @At("RETURN"))
    private void saveCustomData(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        // Save filter inventory - manually serialize each slot
        NbtList filterList = new NbtList();
        for (int i = 0; i < faster_copper_golem$filterInventory.size(); i++) {
            net.minecraft.item.ItemStack stack = faster_copper_golem$filterInventory.getStack(i);
            if (!stack.isEmpty()) {
                NbtCompound slotNbt = new NbtCompound();
                slotNbt.putByte("Slot", (byte) i);
                slotNbt.putString("id", Registries.ITEM.getId(stack.getItem()).toString());
                slotNbt.putInt("Count", stack.getCount());
                filterList.add(slotNbt);
            }
        }
        nbt.put(NBT_FILTER_INVENTORY, filterList);

        // Save chest memory
        NbtList memoryList = new NbtList();
        for (java.util.Map.Entry<Item, BlockPos> entry : faster_copper_golem$chestMemory.entrySet()) {
            NbtCompound entryNbt = new NbtCompound();
            entryNbt.putString("item", Registries.ITEM.getId(entry.getKey()).toString());
            entryNbt.putLong("pos", entry.getValue().asLong());
            memoryList.add(entryNbt);
        }
        nbt.put(NBT_CHEST_MEMORY, memoryList);
    }

    // MODIFIED: NBT Persistence - Load filter inventory and chest memory
    @Inject(method = "readNbt", at = @At("RETURN"))
    private void loadCustomData(NbtCompound nbt, CallbackInfo ci) {
        // Load filter inventory
        nbt.getList(NBT_FILTER_INVENTORY).ifPresent(filterList -> {
            faster_copper_golem$filterInventory.clear();
            for (int i = 0; i < filterList.size(); i++) {
                int finalI = i;
                filterList.getCompound(finalI).ifPresent(slotNbt -> {
                    slotNbt.getByte("Slot").ifPresent(slotByte -> {
                        int slot = slotByte;
                        if (slot >= 0 && slot < faster_copper_golem$filterInventory.size()) {
                            slotNbt.getString("id").ifPresent(itemIdStr -> {
                                Identifier itemId = Identifier.tryParse(itemIdStr);
                                if (itemId != null && Registries.ITEM.containsId(itemId)) {
                                    Item item = Registries.ITEM.get(itemId);
                                    int count = slotNbt.getInt("Count").orElse(1);
                                    net.minecraft.item.ItemStack stack = new net.minecraft.item.ItemStack(item, count);
                                    faster_copper_golem$filterInventory.setStack(slot, stack);
                                }
                            });
                        }
                    });
                });
            }
        });

        // Load chest memory
        nbt.getList(NBT_CHEST_MEMORY).ifPresent(memoryList -> {
            faster_copper_golem$chestMemory.clear();
            for (int i = 0; i < memoryList.size(); i++) {
                int finalI = i;
                memoryList.getCompound(finalI).ifPresent(entryNbt -> {
                    entryNbt.getString("item").ifPresent(itemIdStr -> {
                        Identifier itemId = Identifier.tryParse(itemIdStr);
                        if (itemId != null && Registries.ITEM.containsId(itemId)) {
                            Item item = Registries.ITEM.get(itemId);
                            entryNbt.getLong("pos").ifPresent(posLong -> {
                                BlockPos pos = BlockPos.fromLong(posLong);
                                faster_copper_golem$chestMemory.put(item, pos);
                            });
                        }
                    });
                });
            }
        });
    }
}
