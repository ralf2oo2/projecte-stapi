package net.ralf2oo2.projecte.item;

import net.danygames2014.nyalib.sound.SoundHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.api.item.ItemCharge;
import net.ralf2oo2.projecte.api.item.ItemWithDisplayDurability;
import net.ralf2oo2.projecte.api.item.ProjectileShooter;
import net.ralf2oo2.projecte.entity.LensProjectileEntity;
import net.ralf2oo2.projecte.util.Constants;
import net.ralf2oo2.projecte.util.Sounds;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class HyperkineticLensItem extends ProjectEItem implements ProjectileShooter, ItemCharge, ItemWithDisplayDurability {
    public HyperkineticLensItem(Identifier identifier) {
        super(identifier);
        this.setMaxCount(1);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, PlayerEntity user) {
        if(!world.isRemote) {
            shootProjectile(user, stack);
        }
        return stack;
    }

    @Override
    public boolean shootProjectile(@NotNull PlayerEntity player, @NotNull ItemStack stack) {
        World world = player.world;
        long requiredEmc = Constants.EXPLOSIVE_LENS_COST[this.getCharge(stack)];

        if (!consumeFuel(player, stack, requiredEmc, true))
        {
            return false;
        }

        SoundHelper.playSound(player.world, player.x, player.y, player.z, Sounds.POWER, 1.0F, 1.0F);
        LensProjectileEntity ent = new LensProjectileEntity(world, player, this.getCharge(stack));
        ent.setVelocity(player, player.pitch, player.yaw, 0, 1.5F, 1);
        world.spawnEntity(ent);
        return true;
    }

    @Override
    public int getNumCharges(@Nonnull ItemStack stack)
    {
        return 3;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0D - (double) getCharge(stack) / getNumCharges(stack);
    }
}
