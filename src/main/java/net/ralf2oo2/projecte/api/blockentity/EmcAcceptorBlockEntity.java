package net.ralf2oo2.projecte.api.blockentity;

import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.NotNull;

/**
 * Reference implementation of IEMCAcceptor
 *
 * @author williewillus
 */
public class EmcAcceptorBlockEntity extends EmcBaseBlockEntity implements EmcAcceptor{
    @Override
    public long acceptEMC(@NotNull Direction side, long toAccept)
    {
        long toAdd = Math.min(maximumEMC - currentEMC, toAccept);
        addEMC(toAdd);
        return toAdd;
    }
}
