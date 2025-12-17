package com.pixelindiedev.faster_copper_golem.mixin;

import com.pixelindiedev.faster_copper_golem.mixin.CopperGolemAccessor;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.passive.CopperGolemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

    @Inject(method = "canGather", at = @At("HEAD"), cancellable = true)
    private void checkCopperGolemFilter(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof CopperGolemEntity) {
            CopperGolemAccessor golem = (CopperGolemAccessor) this;
            if (!golem.faster_copper_golem$isItemAllowed(stack.getItem())) {
                cir.setReturnValue(false);
            }
        }
    }
}
