package net.ralf2oo2.projecte.api.blockentity;

import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.NotNull;

/**
 * Reference implementation of both IEMCAcceptor and IEMCProvider
 *
 * @author williewillus
 */
public class EmcHandlerBlockEntity extends EmcBaseBlockEntity implements EmcAcceptor, EmcProvider{
    public EmcHandlerBlockEntity()
    {
        this.maximumEMC = Long.MAX_VALUE;
    }

    public EmcHandlerBlockEntity(long max)
    {
        this.maximumEMC = max;
    }

    // -- IEMCAcceptor -- //
    @Override
    public long acceptEMC(@NotNull Direction side, long toAccept)
    {
        long toAdd = Math.min(maximumEMC - currentEMC, toAccept);
        currentEMC += toAdd;
        return toAdd;
    }

    // -- IEMCProvider -- //
    @Override
    public long provideEMC(@NotNull Direction side, long toExtract)
    {
        long toRemove = Math.min(currentEMC, toExtract);
        currentEMC -= toRemove;
        return toRemove;
    }

    // -- IEMCStorage --//
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
}
