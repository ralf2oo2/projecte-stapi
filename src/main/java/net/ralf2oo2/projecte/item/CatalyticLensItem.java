package net.ralf2oo2.projecte.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.api.item.ProjectileShooter;
import net.ralf2oo2.projecte.listener.ItemListener;
import org.jetbrains.annotations.NotNull;

public class CatalyticLensItem extends DestructionCatalystItem implements ProjectileShooter {
    public CatalyticLensItem(Identifier identifier) {
        super(identifier);
    }

    @Override
    public boolean shootProjectile(@NotNull PlayerEntity player, @NotNull ItemStack stack) {
        return ((ProjectileShooter) ItemListener.hyperLens).shootProjectile(player, stack);
    }

    @Override
    public int getNumCharges(@NotNull ItemStack stack) {
        return 7;
    }
}
