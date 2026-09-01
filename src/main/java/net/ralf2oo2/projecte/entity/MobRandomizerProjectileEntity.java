package net.ralf2oo2.projecte.entity;

import net.danygames2014.nyalib.particle.ParticleHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.ralf2oo2.projecte.util.EMCHelper;
import net.ralf2oo2.projecte.util.LivingEntityHelper;
import net.ralf2oo2.projecte.util.WorldHelper;

public class MobRandomizerProjectileEntity extends ProjectEProjectile{
    public MobRandomizerProjectileEntity(World world)
    {
        super(world);
    }

    public MobRandomizerProjectileEntity(World world, PlayerEntity entity)
    {
        super(world, entity);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.world.isRemote)
        {
            if (age > 400 || this.isSubmergedInWater() || !this.world.isPosLoaded((int) x, (int) y, (int) z))
            {
                this.markDead();
            }
        }
    }

    @Override
    protected void apply(HitResult hit) {
        if (!this.world.isRemote)
        {
            if (this.isSubmergedInWater())
            {
                this.markDead();
                return;
            }
        }

        if (!this.world.isRemote)
        {
            for (int i = 0; i < 4; ++i)
            {
                ParticleHelper.addParticle(world, "portal", this.x, this.y + this.random.nextDouble() * 2.0D, this.z, this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
            }
        }

        if (!(hit.entity instanceof LivingEntity ent))
        {
            return;
        }

        LivingEntity randomized = WorldHelper.getRandomEntity(this.world, ent);

        if (randomized != null && EMCHelper.consumePlayerFuel(((PlayerEntity) getOwner()), 384) != -1)
        {
            ent.markDead();
            randomized.setPositionAndAngles(ent.x, ent.y, ent.z, ent.yaw, ent.pitch);
            this.world.spawnEntity(randomized);
            if(!world.isRemote) {
                LivingEntityHelper.spawnExplosionParticle(ent);
            }
        }
    }

    @Override
    public String getTexture() {
        return "/assets/projecte/stationapi/textures/entity/randomizer.png";
    }
}
