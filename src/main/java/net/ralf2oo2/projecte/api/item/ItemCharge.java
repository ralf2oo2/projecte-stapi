package net.ralf2oo2.projecte.api.item;

import net.danygames2014.nyalib.sound.SoundHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.ralf2oo2.projecte.util.Sounds;
import org.jetbrains.annotations.NotNull;

/**
 * This interface specifies items that have a charge that changes when the respective keybinding is activated (default V)
 */
public interface ItemCharge
{
    String KEY = "Charge";

    int getNumCharges(@NotNull ItemStack stack);
    /**
     * Returns the current charge on the given ItemStack
     * @param stack Stack whose charge we want
     * @return The charge on the stack
     */
    default int getCharge(@NotNull ItemStack stack) {
        return stack.getStationNbt().getInt(KEY);
    }

    /**
     * Called serverside when the player presses the charge keybinding; reading sneaking state is up to you
     * @param player The player
     * @param stack The item being charged
     * @return Whether the operation succeeded
     */
    default boolean changeCharge(@NotNull PlayerEntity player, @NotNull ItemStack stack) {
        int currentCharge = getCharge(stack);
        int numCharges = getNumCharges(stack);

        if (player.isSneaking())
        {
            if (currentCharge > 0)
            {
                SoundHelper.playSound(player, Sounds.UNCHARGE, 1.0F, 0.5F + ((0.5F / (float)numCharges) * currentCharge));
                stack.getStationNbt().putInt(KEY, currentCharge - 1);
                return true;
            }
        }
        else if (currentCharge < numCharges)
        {
            SoundHelper.playSound(player, Sounds.CHARGE, 1.0F, 0.5F + ((0.5F / (float)numCharges) * currentCharge));
            stack.getStationNbt().putInt(KEY, currentCharge + 1);
            return true;
        }

        return false;
    }
}
