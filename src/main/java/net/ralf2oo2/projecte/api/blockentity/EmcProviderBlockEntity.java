package net.ralf2oo2.projecte.api.blockentity;

import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.NotNull;

/**
 * Reference implementation for IEMCProvider
 *
 * @author williewillus
 */

public class EmcProviderBlockEntity extends EmcBaseBlockEntity implements EmcProvider{
    @Override
    public long provideEMC(@NotNull Direction side, long toExtract)
    {
        long toRemove = Math.min(currentEMC, toExtract);
        removeEMC(toRemove);
        return toRemove;
    }
}
