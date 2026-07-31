package net.ralf2oo2.projecte.listener;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.block.*;

public class BlockListener {
    public static Block alchChest;
    public static Block interdictionTorch;
    public static Block transmuteStone;
    public static Block condenser;
    public static Block condenserMk2;
    public static Block rmFurnaceOff;
    public static Block rmFurnaceOn;
    public static Block dmFurnaceOff;
    public static Block dmFurnaceOn;
    public static Block dmPedestal;
    public static Block matterBlock;
    public static Block fuelBlock;
    public static Block collectorMK1;
    public static Block collectorMK2;
    public static Block collectorMK3;
    public static Block relay;
    public static Block relayMK2;
    public static Block relayMK3;
    public static Block novaCatalyst;
    public static Block novaCataclysm;

    @EventListener
    public void registerBlocks(BlockRegistryEvent event) {
        alchChest = new AlchemicalChestBlock(ProjectE.NAMESPACE.id("alchemical_chest"));

        transmuteStone = new TransmutationTableBlock(ProjectE.NAMESPACE.id("transmutation_table"));
        condenser = new EnergyCondenserBlock(ProjectE.NAMESPACE.id("energy_condenser"));
        condenserMk2 = new EnergyCondenserMK2Block(ProjectE.NAMESPACE.id("energy_condenser_mk2"));

        collectorMK1 = new EnergyCollectorBlock(ProjectE.NAMESPACE.id("energy_collector_mk1"), 1);
        collectorMK2 = new EnergyCollectorBlock(ProjectE.NAMESPACE.id("energy_collector_mk2"), 2);
        collectorMK3 = new EnergyCollectorBlock(ProjectE.NAMESPACE.id("energy_collector_mk3"), 3);
    }
}
