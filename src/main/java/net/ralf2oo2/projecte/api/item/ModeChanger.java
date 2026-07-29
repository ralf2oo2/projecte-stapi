package net.ralf2oo2.projecte.api.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This interface specifies items that switch between modes when the mode switch keybind is activated (default G)
 */
public interface ModeChanger
{
    /**
     * Gets the mode from this ItemStack
     * @param stack The stack we want the mode of
     * @return The mode of this ItemStack
     */
    byte getMode(@NotNull ItemStack stack);

    /**
     * Called serverside when the player presses change mode
     * @param player The player pressing the change mode key
     * @param stack The stack whose mode we are changing
     * @return Whether the operation succeeded
     */
    boolean changeMode(@NotNull PlayerEntity player, @NotNull ItemStack stack);
}
