package com.pixelindiedev.faster_copper_golem.mixin;

import net.minecraft.entity.ai.brain.task.MoveItemsTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MoveItemsTask.class)
public interface MoveItemsTaskAccessor {
    @Accessor("horizontalRange")
    void setHorizontalRange(int value);

    @Accessor("verticalRange")
    void setVerticalRange(int value);

    @Accessor("speed")
    void setSpeed(float value);
}
