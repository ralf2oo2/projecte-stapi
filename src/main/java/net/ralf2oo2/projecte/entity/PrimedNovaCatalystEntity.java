package net.ralf2oo2.projecte.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.world.World;
import net.ralf2oo2.projecte.util.WorldHelper;

public class PrimedNovaCatalystEntity extends TntEntity {
    public LivingEntity source;

    public PrimedNovaCatalystEntity(World world) {
        super(world);
        this.fuse = 20;
    }

    public PrimedNovaCatalystEntity(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.fuse = 20;
    }
    public PrimedNovaCatalystEntity(World world, double x, double y, double z, LivingEntity source) {
        this(world, x, y, z);
        this.fuse = 20;
        this.source = source;
    }


    @Override
    public void tick() {
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        this.velocityY -= 0.04F;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= 0.98F;
        this.velocityY *= 0.98F;
        this.velocityZ *= 0.98F;
        if (this.onGround) {
            this.velocityX *= 0.7F;
            this.velocityZ *= 0.7F;
            this.velocityY *= -0.5F;
        }

        if (this.fuse-- <= 0) {
            if (!this.world.isRemote) {
                this.markDead();
                this.explode();
            } else {
                this.markDead();
            }
        } else {
            this.world.addParticle("smoke", this.x, this.y + (double)0.5F, this.z, 0.0F, 0.0F, 0.0F);
        }

    }

    protected void explode() {
        WorldHelper.createNovaExplosion(world, this, x, y, z, 16.0F);
    }
}
