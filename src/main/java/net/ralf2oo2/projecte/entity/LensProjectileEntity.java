package net.ralf2oo2.projecte.entity;

import net.danygames2014.nyalib.particle.ParticleHelper;
import net.danygames2014.nyalib.sound.SoundHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.ralf2oo2.projecte.util.Constants;
import net.ralf2oo2.projecte.util.WorldHelper;

public class LensProjectileEntity extends ProjectEProjectile {
    private int charge;

    public LensProjectileEntity(World world)
    {
        super(world);
    }

    public LensProjectileEntity(World world, PlayerEntity entity, int charge)
    {
        super(world, entity);
        this.charge = charge;
    }

    public LensProjectileEntity(World world, double x, double y, double z, int charge)
    {
        super(world, x, y, z);
        this.charge = charge;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isRemote)
        {
            return;
        }

        if (age > 400 || !this.world.isPosLoaded((int) x, (int) y, (int) z))
        {
            this.markDead();
            return;
        }

        if (this.isSubmergedInWater())
        {
            SoundHelper.playSound(world, "random.fizz", 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
            ParticleHelper.addParticle(world, "smoke", x, y, z, 0, 0, 0);
            this.markDead();
        }
    }

    @Override
    protected void apply(HitResult hit) {
        if(world.isRemote) {
            return;
        }
        WorldHelper.createNovaExplosion(world, getOwner(), x, y, z, Constants.EXPLOSIVE_LENS_RADIUS[charge]);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("Charge", charge);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        charge = nbt.getInt("Charge");
    }

    @Override
    public String getTexture() {
        return "/assets/projecte/stationapi/textures/entity/lens_explosive.png";
    }
}
