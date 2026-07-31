package net.ralf2oo2.projecte.listener;

import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.client.gui.screen.GuiHandler;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.util.Namespace;
import net.ralf2oo2.projecte.api.capability.AlchemicalBagEntityCapability;
import net.ralf2oo2.projecte.block.entity.*;
import net.ralf2oo2.projecte.client.gui.screen.*;
import net.ralf2oo2.projecte.inventory.SimpleInventory;
import net.ralf2oo2.projecte.inventory.TransmutationInventory;
import net.ralf2oo2.projecte.item.AlchemicalBagItem;

public class ScreenHandlerListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerScreenHandlers(GuiHandlerRegistryEvent event) {
        event.register(NAMESPACE.id("transmutation"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage)this::openTransmutationScreen, () -> null));
        event.register(NAMESPACE.id("alchemical_chest"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage)this::openAlchemicalChestScreen, AlchemicalChestBlockEntity::new));
        event.register(NAMESPACE.id("alchemical_bag"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage)this::openAlchemicalBagScreen, () -> null));

        event.register(NAMESPACE.id("energy_condenser"), new GuiHandler(this::openEnergyCondenserScreen, () -> null));
        event.register(NAMESPACE.id("energy_condenser_mk2"), new GuiHandler(this::openEnergyCondenserMK2Screen, () -> null));

        event.register(NAMESPACE.id("energy_collector_mk1"), new GuiHandler(this::openEnergyCollectorMK1Screen, () -> null));
        event.register(NAMESPACE.id("energy_collector_mk2"), new GuiHandler(this::openEnergyCollectorMK2Screen, () -> null));
        event.register(NAMESPACE.id("energy_collector_mk3"), new GuiHandler(this::openEnergyCollectorMK3Screen, () -> null));
    }

    private Screen openTransmutationScreen(PlayerEntity player, Inventory inventory) {
        return new TransmutationScreen(player.inventory, new TransmutationInventory(player));
    }

    private Screen openAlchemicalChestScreen(PlayerEntity player, Inventory inventory) {
        return new AlchemicalChestScreen(player.inventory, (AlchemicalChestBlockEntity) inventory);
    }

    private Screen openAlchemicalBagScreen(PlayerEntity player, Inventory inventory) {
        AlchemicalBagEntityCapability capability = CapabilityHelper.getCapability(player, AlchemicalBagEntityCapability.class);
        if(player.getHand() != null && capability != null &&  player.getHand().getItem() instanceof AlchemicalBagItem bag) {
            return new AlchemicalChestScreen(player.inventory, capability.getBag(bag.color));
        }
        return null;
    }

    private Screen openEnergyCondenserScreen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[1], message.ints[2], message.ints[3]);
        if(blockEntity instanceof EnergyCondenserBlockEntity condenser) {
            return new EnergyCondenserScreen(player.inventory, condenser);
        }
        return null;
    }

    private Screen openEnergyCondenserMK2Screen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[1], message.ints[2], message.ints[3]);
        if(blockEntity instanceof EnergyCondenserMK2BlockEntity condenser) {
            return new EnergyCondenserMK2Screen(player.inventory, condenser);
        }
        return null;
    }

    private Screen openEnergyCollectorMK1Screen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[1], message.ints[2], message.ints[3]);
        if(blockEntity instanceof EnergyCollectorMK1BlockEntity collector) {
            return new EnergyCollectorMK1Screen(player.inventory, collector);
        }
        return null;
    }

    private Screen openEnergyCollectorMK2Screen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[1], message.ints[2], message.ints[3]);
        if(blockEntity instanceof EnergyCollectorMK2BlockEntity collector) {
            return new EnergyCollectorMK2Screen(player.inventory, collector);
        }
        return null;
    }

    private Screen openEnergyCollectorMK3Screen(PlayerEntity player, Inventory inventory, MessagePacket message) {
        BlockEntity blockEntity = player.world.getBlockEntity(message.ints[1], message.ints[2], message.ints[3]);
        if(blockEntity instanceof EnergyCollectorMK3BlockEntity collector) {
            return new EnergyCollectorMK3Screen(player.inventory, collector);
        }
        return null;
    }
}
