package net.ralf2oo2.projecte.api.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This interface specifies items that perform a specific function when the Extra Function key is activated (default C)
 */
public interface ExtraFunction
{
    /**
     * Called serverside when the server receives a Extra Function key packet
     * @param stack The ItemStack performing this function
     * @param player The player performing this function
     * @return Whether the operation succeeded
     */
    boolean doExtraFunction(@NotNull ItemStack stack, @NotNull PlayerEntity player);
}
