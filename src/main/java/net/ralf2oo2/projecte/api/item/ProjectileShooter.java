package net.ralf2oo2.projecte.api.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This interface specifies items that fire a projectile when the Shoot Projectile keybind is activated (default R)
 */
public interface ProjectileShooter
{
    /**
     * Called serverside when the player presses the Fire Projectile Button
     * @param player The player pressing the key
     * @param stack The stack we are using to shoot
     * @return If the projectile was actually fired
     */
    boolean shootProjectile(@NotNull PlayerEntity player, @NotNull ItemStack stack);
}
