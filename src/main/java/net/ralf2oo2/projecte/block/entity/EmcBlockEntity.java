package net.ralf2oo2.projecte.block.entity;

import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.ralf2oo2.projecte.api.blockentity.EmcAcceptor;
import net.ralf2oo2.projecte.api.blockentity.EmcBaseBlockEntity;
import net.ralf2oo2.projecte.api.blockentity.EmcProvider;
import net.ralf2oo2.projecte.util.Constants;
import net.ralf2oo2.projecte.util.WorldHelper;

import java.util.Map;

public class EmcBlockEntity extends EmcBaseBlockEntity {
    public EmcBlockEntity() {
        setMaximumEMC(Constants.TILE_MAX_EMC);
    }

    public EmcBlockEntity(long maxAmount) {
        setMaximumEMC(maxAmount);
    }

    protected boolean hasMaxedEmc()
    {
        return getStoredEmc() >= getMaximumEmc();
    }

    /**
     * The amount provided will be divided and evenly distributed as best as possible between adjacent IEMCAcceptors
     * Remainder or rejected EMC is added back to this provider
     *
     * @param emc The maximum combined emc to send to others
     */
    protected void sendToAllAcceptors(long emc)
    {
        if (!(this instanceof EmcProvider))
        {
            // todo move this method somewhere
            throw new UnsupportedOperationException("sending without being a provider");
        }


        Map<Direction, BlockEntity> blockEntities = Maps.filterValues(WorldHelper.getAdjacentBlockEntitiesMapped(world, this), Predicates.instanceOf(EmcAcceptor.class));
        if (blockEntities.isEmpty())
        {
            return;
        }

        long emcPer = emc / blockEntities.size();
        for (Map.Entry<Direction, BlockEntity> entry : blockEntities.entrySet())
        {
            // TODO: implement later
//            if (this instanceof RelayMK1Tile && entry.getValue() instanceof RelayMK1Tile)
//            {
//                continue;
//            }
            long provide = ((EmcProvider) this).provideEMC(entry.getKey().getOpposite(), emcPer);
            long remain = provide - ((EmcAcceptor) entry.getValue()).acceptEMC(entry.getKey(), provide);
            this.addEMC(remain);
        }
    }
}
