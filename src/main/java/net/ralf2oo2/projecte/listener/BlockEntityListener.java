package net.ralf2oo2.projecte.listener;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.block.entity.*;

public class BlockEntityListener {
    @EventListener
    public void registerBlockEntities(BlockEntityRegisterEvent event) {
        event.register(ProjectE.NAMESPACE.id("alchemical_chest"), AlchemicalChestBlockEntity.class);

        event.register(ProjectE.NAMESPACE.id("energy_condenser"), EnergyCondenserBlockEntity.class);
        event.register(ProjectE.NAMESPACE.id("energy_condenser_mk2"), EnergyCondenserMK2BlockEntity.class);

        event.register(ProjectE.NAMESPACE.id("energy_collector_mk1"), EnergyCollectorMK1BlockEntity.class);
        event.register(ProjectE.NAMESPACE.id("energy_collector_mk2"), EnergyCollectorMK2BlockEntity.class);
        event.register(ProjectE.NAMESPACE.id("energy_collector_mk3"), EnergyCollectorMK3BlockEntity.class);

        event.register(ProjectE.NAMESPACE.id("dark_matter_relay_mk1"), AntiMatterRelayMK1BlockEntity.class);
        event.register(ProjectE.NAMESPACE.id("dark_matter_relay_mk2"), AntiMatterRelayMK2BlockEntity.class);
        event.register(ProjectE.NAMESPACE.id("dark_matter_relay_mk3"), AntiMatterRelayMK3BlockEntity.class);
    }
}
