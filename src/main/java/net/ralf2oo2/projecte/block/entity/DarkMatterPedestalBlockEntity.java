package net.ralf2oo2.projecte.block.entity;

import net.danygames2014.nyalib.particle.ParticleHelper;
import net.danygames2014.nyalib.sound.SoundHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.ralf2oo2.projecte.api.item.PedestalItem;
import net.ralf2oo2.projecte.util.InventoryHelper;
import net.ralf2oo2.projecte.util.Sounds;
import net.ralf2oo2.projecte.util.StackUtil;

import java.util.Random;

public class DarkMatterPedestalBlockEntity extends EmcBlockEntity{
    private static final int RANGE = 4;
    private boolean isActive = false;
    private Inventory inventory = new SimpleInventory("inv", 1);
    private int particleCooldown = 10;
    private int activityCooldown = 0;
    public boolean previousRedstoneState = false;
    public double centeredX, centeredY, centeredZ;

    @Override
    public void tick() {
        centeredX = x + 0.5;
        centeredY = y + 0.5;
        centeredZ = z + 0.5;

        if (getActive())
        {
            if (!StackUtil.isEmpty(inventory.getStack(0)))
            {
                Item item = inventory.getStack(0).getItem();
                if (item instanceof PedestalItem pedestalItem)
                {
                    pedestalItem.updateInPedestal(world, new BlockPos(x, y, z));
                }
                if (particleCooldown <= 0)
                {
                    spawnParticles();
                    particleCooldown = 10;
                }
                else
                {
                    particleCooldown--;
                }
            }
            else
            {
                setActive(false);
            }
        }
    }

    private void spawnParticles()
    {
        int x = this.x;
        int y = this.y;
        int z = this.z;

        ParticleHelper.addParticle(world, "flame", x + 0.2, y + 0.3, z + 0.2, 0, 0, 0);
        ParticleHelper.addParticle(world, "flame", x + 0.2, y + 0.3, z + 0.5, 0, 0, 0);
        ParticleHelper.addParticle(world, "flame", x + 0.2, y + 0.3, z + 0.8, 0, 0, 0);
        ParticleHelper.addParticle(world, "flame", x + 0.5, y + 0.3, z + 0.2, 0, 0, 0);
        ParticleHelper.addParticle(world, "flame", x + 0.5, y + 0.3, z + 0.8, 0, 0, 0);
        ParticleHelper.addParticle(world, "flame", x + 0.8, y + 0.3, z + 0.2, 0, 0, 0);
        ParticleHelper.addParticle(world, "flame", x + 0.8, y + 0.3, z + 0.5, 0, 0, 0);
        ParticleHelper.addParticle(world, "flame", x + 0.8, y + 0.3, z + 0.8, 0, 0, 0);

        Random rand = world.random;
        for (int i = 0; i < 3; ++i)
        {
            int j = rand.nextInt(2) * 2 - 1;
            int k = rand.nextInt(2) * 2 - 1;
            double d0 = (double)this.x + 0.5D + 0.25D * (double)j;
            double d1 = (float)this.y + rand.nextFloat();
            double d2 = (double)this.z + 0.5D + 0.25D * (double)k;
            double d3 = rand.nextFloat() * (float)j;
            double d4 = ((double)rand.nextFloat() - 0.5D) * 0.125D;
            double d5 = rand.nextFloat() * (float)k;
            ParticleHelper.addParticle(world, "portal", d0, d1, d2, d3, d4, d5);
        }
    }

    public int getActivityCooldown()
    {
        return activityCooldown;
    }

    public void setActivityCooldown(int i)
    {
        activityCooldown = i;
    }

    public void decrementActivityCooldown()
    {
        activityCooldown--;
    }

    /**
     * @return Inclusive bounding box of all positions this pedestal should apply effects in
     */
    public Box getEffectBounds()
    {
        return Box.create(x - RANGE, y - RANGE, z - RANGE, x + RANGE, y + RANGE, z + RANGE);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.inventory = new SimpleInventory("inv", 1);
        InventoryHelper.readNbtList(tag.getList("inv"), inventory);
        setActive(tag.getBoolean("isActive"));
        this.activityCooldown = tag.getInt("activityCooldown");
        this.previousRedstoneState = tag.getBoolean("powered");
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.put("inv", InventoryHelper.toNbtList(inventory));
        tag.putBoolean("isActive", getActive());
        tag.putInt("activityCooldown", activityCooldown);
        tag.putBoolean("powered", previousRedstoneState);
    }

    public boolean getActive()
    {
        return isActive;
    }

    public void setActive(boolean newState)
    {
        if (newState != this.getActive() && world != null)
        {
            if (newState)
            {
                SoundHelper.playSound(world, x, y, z, Sounds.CHARGE, 1.0F, 1.0F);
                for (int i = 0; i < world.random.nextInt(35) + 10; ++i)
                {
                    ParticleHelper.addParticle(world, "reddust", centeredX + world.random.nextGaussian() * 0.12999999523162842D,
                            y + 1 + world.random.nextGaussian() * 0.12999999523162842D,
                            centeredZ + world.random.nextGaussian() * 0.12999999523162842D,
                            0.62D, 0.0D, 0.62D);
                }
            }
            else
            {
                SoundHelper.playSound(world, x, y, z, Sounds.UNCHARGE, 1.0F, 1.0F);
                for (int i = 0; i < world.random.nextInt(35) + 10; ++i)
                {
                    ParticleHelper.addParticle(world, "smoke", centeredX + world.random.nextGaussian() * 0.12999999523162842D,
                            y + 1 + world.random.nextGaussian() * 0.12999999523162842D,
                            centeredZ + world.random.nextGaussian() * 0.12999999523162842D,
                            0.0D, 0.0D, 0.0D);
                }
            }
        }
        this.isActive = newState;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
