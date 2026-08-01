package net.ralf2oo2.projecte.util;

import com.google.common.collect.Sets;
import net.danygames2014.nyalib.particle.ParticleHelper;
import net.danygames2014.nyalib.sound.SoundHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.block.States;
import net.ralf2oo2.projecte.entity.PrimedNovaCatalystEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class NovaExplosion extends Explosion {
    private final World world;
    public NovaExplosion(World world, Entity source, double x, double y, double z, float power) {
        super(world, source, x, y, z, power);
        this.world = world;
    }

    @Override
    public void explode() {
        float initialSize = this.power;

        HashSet<BlockPos> hashset = Sets.newHashSet();
        int j;
        int k;

        for (int i = 0; i < 16; ++i)
        {
            for (j = 0; j < 16; ++j)
            {
                for (k = 0; k < 16; ++k)
                {
                    if (i == 0 || i == 15 || j == 0 || j == 15 || k == 0 || k == 15)
                    {
                        double d0 = (float)i / 15.0F * 2.0F - 1.0F;
                        double d1 = (float)j / 15.0F * 2.0F - 1.0F;
                        double d2 = (float)k / 15.0F * 2.0F - 1.0F;
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f = initialSize * (0.7F + this.world.random.nextFloat() * 0.6F);
                        double d4 = this.x;
                        double d6 = this.y;
                        double d8 = this.z;

                        for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F)
                        {
                            BlockPos blockpos = new BlockPos((int)d4, (int)d6, (int)d8);
                            BlockState blockstate = this.world.getBlockState(blockpos);

                            if (blockstate != States.AIR)
                            {
                                float f2 = blockstate.getBlock().getBlastResistance(this.source);
                                f -= (f2 + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F)
                            {
                                hashset.add(blockpos);
                            }

                            d4 += d0 * 0.30000001192092896D;
                            d6 += d1 * 0.30000001192092896D;
                            d8 += d2 * 0.30000001192092896D;
                        }
                    }
                }
            }
        }

        this.damagedBlocks.addAll(hashset);
        this.power = initialSize;
//        net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.worldObj, this, Collections.emptyList(), this.size);
    }

    @Override
    public void playExplosionSound(boolean addParticles) {
        float cachedExplosionSize = this.power;

        SoundHelper.playSound(world, x, y, z, "random.explode", 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F);

        if (cachedExplosionSize >= 2.0F)
        {
            ParticleHelper.addParticle(world, "explode", x, y, z, 1.0D, 0.0D, 0.0D);
        }
        else
        {
            ParticleHelper.addParticle(world, "explode", x, y, z, 1.0D, 0.0D, 0.0D);
        }

        Iterator<BlockPos> iterator;
        BlockPos blockpos;
        List<ItemStack> allDrops = new ArrayList<>();

        iterator = damagedBlocks.iterator();

        while (iterator.hasNext())
        {
            blockpos = iterator.next();
            BlockState state = world.getBlockState(blockpos);
            Block block = state.getBlock();

            if (addParticles)
            {
                double d0 = (float)blockpos.getX() + this.world.random.nextFloat();
                double d1 = (float)blockpos.getY() + this.world.random.nextFloat();
                double d2 = (float)blockpos.getZ() + this.world.random.nextFloat();
                double d3 = d0 - x;
                double d4 = d1 - y;
                double d5 = d2 - z;
                double d6 = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                d3 /= d6;
                d4 /= d6;
                d5 /= d6;
                double d7 = 0.5D / (d6 / (double)cachedExplosionSize + 0.1D);
                d7 *= (this.world.random.nextFloat() * this.world.random.nextFloat() + 0.3F);
                d3 *= d7;
                d4 *= d7;
                d5 *= d7;
                ParticleHelper.addParticle(world, "explode", (d0 + x) / 2.0D, (d1 + y) / 2.0D, (d2 + z) / 2.0D, d3, d4, d5);
                ParticleHelper.addParticle(world, "smoke", d0, d1, d2, d3, d4, d5);
            }

            if (state != States.AIR.get())
            {
                int meta = world.getBlockMeta(blockpos.x, blockpos.y, blockpos.z);
                int dropCount = block.getDroppedItemCount(world.random);
                int itemId = block.getDroppedItemId(meta, world.random);
                List<ItemStack> drops = block.getDropList(world, blockpos.x, blockpos.y, blockpos.z, this.world.getBlockState(blockpos), meta);
                if (drops != null && !drops.isEmpty())
                {
                    allDrops.addAll(drops);
                } else if(dropCount > 0) {
                    allDrops.add(new ItemStack(Item.ITEMS[itemId], dropCount, meta));
                }

                block.onDestroyedByExplosion(world, blockpos.x, blockpos.y, blockpos.z);
                world.setBlockState(blockpos, States.AIR.get());
            }
        }
        if (getExplosivePlacedBy() != null)
        {
            WorldHelper.createLootDrop(allDrops, this.world, new BlockPos((int) getExplosivePlacedBy().x, (int) getExplosivePlacedBy().y, (int) getExplosivePlacedBy().z));
        }
        else
        {
            WorldHelper.createLootDrop(allDrops, this.world, x, y, z);
        }
    }

    public LivingEntity getExplosivePlacedBy() {
        if(this.source == null) {
            return null;
        } else if(this.source instanceof PrimedNovaCatalystEntity primed) {
            return primed.source;
        } else {
            return this.source instanceof LivingEntity livingEntity ? livingEntity : null;
        }
    }
}
