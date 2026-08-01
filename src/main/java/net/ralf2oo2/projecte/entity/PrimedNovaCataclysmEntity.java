package net.ralf2oo2.projecte.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.world.World;
import net.ralf2oo2.projecte.util.WorldHelper;

public class PrimedNovaCataclysmEntity extends PrimedNovaCatalystEntity {
    public LivingEntity source;

    public PrimedNovaCataclysmEntity(World world) {
        super(world);;
    }

    public PrimedNovaCataclysmEntity(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    public PrimedNovaCataclysmEntity(World world, double x, double y, double z, LivingEntity source) {
        super(world, x, y, z, source);
    }

    protected void explode() {
        WorldHelper.createNovaExplosion(world, this, x, y, z, 48.0F);
    }
}
