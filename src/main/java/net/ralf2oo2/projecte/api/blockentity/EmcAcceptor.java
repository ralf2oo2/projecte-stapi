package net.ralf2oo2.projecte.api.blockentity;

import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.NotNull;

/**
 * Implement this interface to specify that "EMC can be given to this Tile Entity from an external source"
 * The contract of this interface is only the above statement
 * However, ProjectE implements an "active-push" system, where providers automatically send EMC to acceptors. You are recommended to follow this convention
 * Reference implementation provided in TileEmcHandler
 *
 * @author williewillus
 */
public interface EmcAcceptor extends EmcStorage
{
    /**
     * Accept, at most, the given amount of EMC from the given side
     * @param side The side to accept EMC from
     * @param toAccept The maximum amount to accept
     * @return The amount actually accepted
     */
    long acceptEMC(@NotNull Direction side, long toAccept);
}
