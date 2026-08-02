package net.ralf2oo2.projecte.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * Internal interface for PlayerChecks.
 */
public interface StepAssister {
    /**
     * @return If this stack currently should enhance the bearer's step height
     */
    boolean canAssistStep(ItemStack stack, PlayerEntity player);
}
