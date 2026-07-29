package net.ralf2oo2.projecte.api.blockentity;

import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.NotNull;

/**
 * Implement this interface to specify that "EMC can be taken from this Tile Entity from an external source"
 * The contract of this interface is limited to only the above statement
 * However, ProjectE implements an "active-push" system, where providers automatically send EMC to acceptors. You are recommended to follow this convention
 * Reference implementation provided in TileEmcHandler
 *
 * @author williewillus
 */
public interface EmcProvider extends EmcStorage
{
    /**
     * Extract, at most, the given amount of EMC from the given side
     * @param side The side to extract EMC from
     * @param toExtract The maximum amount to extract
     * @return The amount actually extracted
     */
    long provideEMC(@NotNull Direction side, long toExtract);
}
