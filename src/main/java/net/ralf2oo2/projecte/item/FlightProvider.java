package net.ralf2oo2.projecte.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * Internal interface for PlayerChecks.
 */
public interface FlightProvider {
    /**
     * @return If this stack currently should provide its bearer flight
     */
    boolean canProvideFlight(ItemStack stack, PlayerEntity player);
}
