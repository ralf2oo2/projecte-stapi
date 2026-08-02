package net.ralf2oo2.projecte.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * Internal interface for PlayerChecks.
 */
public interface FireProtector {
    /**
     * @return If this stack currently should protect the bearer from fire
     */
    boolean canProtectAgainstFire(ItemStack stack, PlayerEntity player);
}
