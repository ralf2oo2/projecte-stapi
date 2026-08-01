package net.ralf2oo2.projecte.api.item;

import net.minecraft.item.ItemStack;

public interface ItemWithDisplayDurability {
    double getDurabilityForDisplay(ItemStack stack);
}
