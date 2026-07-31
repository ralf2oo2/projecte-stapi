package net.ralf2oo2.projecte.listener;

import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.client.gui.screen.GuiHandler;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;
import net.ralf2oo2.projecte.api.capability.AlchemicalBagEntityCapability;
import net.ralf2oo2.projecte.block.entity.AlchemicalChestBlockEntity;
import net.ralf2oo2.projecte.client.gui.screen.AlchemicalChestScreen;
import net.ralf2oo2.projecte.client.gui.screen.TransmutationScreen;
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
}
