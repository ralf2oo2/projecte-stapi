package net.ralf2oo2.projecte.api.blockentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

/**
 * Base class for the reference implementations TileEmcProvider, TileEmcAcceptor, and TileEmcHandler
 * Usually you want to use one of three derived reference implementations
 * Extend this if you want fine-grained control over all aspects of how your tile provides or accepts EMC
 *
 * @author williewillus
 */
public class EmcBaseBlockEntity extends BlockEntity implements EmcStorage {
    protected long maximumEMC;
    protected long currentEMC = 0;

    protected EmcBaseBlockEntity()
    {
        setMaximumEMC(Long.MAX_VALUE);
    }

    public final void setMaximumEMC(long max)
    {
        maximumEMC = max;
        if (currentEMC > maximumEMC)
        {
            currentEMC = maximumEMC;
        }
    }

    @Override
    public long getStoredEmc()
    {
        return currentEMC;
    }

    @Override
    public long getMaximumEmc()
    {
        return maximumEMC;
    }

    /**
     * Add EMC directly into the internal buffer. Use for internal implementation of your tile
     */
    protected void addEMC(long toAdd)
    {
        currentEMC += toAdd;
        if (currentEMC > maximumEMC)
        {
            currentEMC = maximumEMC;
        }
    }

    /**
     * Removes EMC directly into the internal buffer. Use for internal implementation of your tile
     */
    protected void removeEMC(long toRemove)
    {
        currentEMC -= toRemove;
        if (currentEMC < 0)
        {
            currentEMC = 0;
        }
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        if (currentEMC > maximumEMC)
        {
            currentEMC = maximumEMC;
        }
        tag.putLong("EMC", currentEMC);
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        long set = tag.getLong("EMC");
        if (set > maximumEMC)
        {
            set = maximumEMC;
        }
        currentEMC = set;
    }
}
