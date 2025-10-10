package com.pixelindiedev.faster_copper_golem.mixin;

import net.minecraft.entity.ai.brain.task.MoveItemsTask;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.pixelindiedev.faster_copper_golem.Faster_copper_golem.*;

@Mixin(value = MoveItemsTask.class, priority = 1015)
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

    @ModifyConstant(method = "extractStack", constant = @Constant(intValue = 16))
    private static int increaseStackAmount(int original) {
        return getMaxStackSize();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void captureData(float speed, Predicate inputContainerPredicate, Predicate outputChestPredicate, int horizontalRange, int verticalRange, Map interactionCallbacks, Consumer travellingCallback, Predicate storagePredicate, CallbackInfo ci) {
        this.horizontalRange = getHorizontalSearchRadius();
        this.verticalRange = getVerticalSearchRadius();
        this.speed = getMovementSpeed();

        AddTask((MoveItemsTask) (Object) this);
    }

    @ModifyConstant(method = "tickInteracting", constant = @Constant(intValue = 60))
    private int reduceInteractionTime(int original) {
        return getInteractionTime();
    }

    @ModifyConstant(method = "cooldown", constant = @Constant(intValue = 140))
    private int reduceCooldown(int original) {
        return getCooldownTime();
    }

    @ModifyConstant(method = "markVisited", constant = @Constant(intValue = 10))
    private int increaseVisitedChestMemory(int original) {
        return getMaxChestsRemembered();
    }
}
