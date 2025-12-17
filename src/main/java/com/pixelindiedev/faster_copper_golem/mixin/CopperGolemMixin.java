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

@Mixin(value = CopperGolemEntity.class, priority = 800)
public abstract class CopperGolemMixin implements CopperGolemAccessor {
    // MODIFIED: Memory and Filter Storage
    @Unique
    private final java.util.Map<net.minecraft.item.Item, net.minecraft.util.math.BlockPos> faster_copper_golem$chestMemory = new java.util.HashMap<>();
    @Unique
    private final java.util.Set<net.minecraft.item.Item> faster_copper_golem$pickupFilter = new java.util.HashSet<>();

    @Unique
    @Override
    public java.util.Set<net.minecraft.item.Item> faster_copper_golem$getPickupFilter() {
        return faster_copper_golem$pickupFilter;
    }

    @Unique
    @Override
    public void faster_copper_golem$toggleFilter(net.minecraft.item.Item item) {
        if (faster_copper_golem$pickupFilter.contains(item)) {
            faster_copper_golem$pickupFilter.remove(item);
        } else {
            faster_copper_golem$pickupFilter.add(item);
        }
    }

    @Unique
    @Override
    public boolean faster_copper_golem$isItemAllowed(net.minecraft.item.Item item) {
        // Blacklist logic: If in filter, NOT allowed.
        return !faster_copper_golem$pickupFilter.contains(item);
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

    // MODIFIED: Interaction to toggle filter (Shift-Right-Click)
    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(net.minecraft.entity.player.PlayerEntity player, net.minecraft.util.Hand hand,
            CallbackInfoReturnable<net.minecraft.util.ActionResult> cir) {
        if (player.isSneaking() && hand == net.minecraft.util.Hand.MAIN_HAND) {
            net.minecraft.item.ItemStack stack = player.getStackInHand(hand);
            if (!stack.isEmpty()) {
                net.minecraft.item.Item item = stack.getItem();
                faster_copper_golem$toggleFilter(item);

                // Visual feedback (Particles)
                boolean blocked = faster_copper_golem$pickupFilter.contains(item);
                if (blocked) {
                    // Smoke = Blocked
                    for (int i = 0; i < 5; i++)
                        self.getWorld().addParticle(net.minecraft.particle.ParticleTypes.SMOKE, self.getX(),
                                self.getY() + 0.5, self.getZ(), 0, 0.1, 0);
                } else {
                    // Happy = Allowed
                    for (int i = 0; i < 5; i++)
                        self.getWorld().addParticle(net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER, self.getX(),
                                self.getY() + 0.5, self.getZ(), 0, 0.1, 0);
                }

                cir.setReturnValue(net.minecraft.util.ActionResult.SUCCESS);
            }
        }
    }

    // MODIFIED: Prevent pickup if filtered
    @Inject(method = "canGather", at = @At("HEAD"), cancellable = true)
    private void checkFilter(net.minecraft.item.ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!faster_copper_golem$isItemAllowed(stack.getItem())) {
            cir.setReturnValue(false);
        }
    }

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
}
