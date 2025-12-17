package com.pixelindiedev.faster_copper_golem.mixin;

import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import java.util.Set;

public interface CopperGolemAccessor {
    // MODIFIED: Inventory accessor
    net.minecraft.inventory.SimpleInventory faster_copper_golem$getFilterInventory();

    // Memory methods could be added here if needed to be public
    void faster_copper_golem$rememberChest(Item item, BlockPos pos);

    BlockPos faster_copper_golem$getRememberedChest(Item item);

    boolean faster_copper_golem$isItemAllowed(Item item);
}
