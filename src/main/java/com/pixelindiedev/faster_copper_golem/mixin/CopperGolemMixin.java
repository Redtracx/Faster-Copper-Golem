package com.pixelindiedev.faster_copper_golem.mixin;

import com.pixelindiedev.faster_copper_golem.config.ClientConfigCache;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CopperGolemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CopperGolemEntity.class, priority = 1004)
public abstract class CopperGolemMixin {
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

    @ModifyConstant(method = "clientTick", constant = @Constant(floatValue = 10.0F))
    private float editSpinHeadTimerPlus(float original) {
        return original * ClientConfigCache.speedMultiplier;
    }

    @ModifyConstant(method = "clientTick", constant = @Constant(intValue = 200))
    private int editRandomNext(int original) {
        return (int) (original * ClientConfigCache.speedMultiplier);
    }

    @ModifyConstant(method = "clientTick", constant = @Constant(intValue = 240))
    private int editRandomNext2(int original) {
        return (int) (original * ClientConfigCache.speedMultiplier);
    }

    @Unique
    private void accelerate(AnimationState state, int speed) {
        if (state.isRunning()) {
            if (speed > 4) {
                state.stop();
            } else {
                state.skip(1, speed - 1);
            }
        }
    }
}
