package net.ralf2oo2.projecte.listener;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.block.entity.AlchemicalChestBlockEntity;
import net.ralf2oo2.projecte.block.entity.EnergyCondenserBlockEntity;
import net.ralf2oo2.projecte.block.entity.EnergyCondenserMK2BlockEntity;

public class BlockEntityListener {
    @EventListener
    public void registerBlockEntities(BlockEntityRegisterEvent event) {
        event.register(ProjectE.NAMESPACE.id("alchemical_chest"), AlchemicalChestBlockEntity.class);
        event.register(ProjectE.NAMESPACE.id("energy_condenser"), EnergyCondenserBlockEntity.class);
        event.register(ProjectE.NAMESPACE.id("energy_condenser_mk2"), EnergyCondenserMK2BlockEntity.class);
    }
}
