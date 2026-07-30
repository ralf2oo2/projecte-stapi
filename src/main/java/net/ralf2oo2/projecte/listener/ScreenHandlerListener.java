package net.ralf2oo2.projecte.listener;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.client.gui.screen.GuiHandler;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;
import net.ralf2oo2.projecte.client.gui.screen.TransmutationScreen;
import net.ralf2oo2.projecte.inventory.TransmutationInventory;

public class ScreenHandlerListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerScreenHandlers(GuiHandlerRegistryEvent event) {
        event.register(NAMESPACE.id("transmutation"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage)this::openTransmutationScreen, () -> null));
    }

    private Screen openTransmutationScreen(PlayerEntity player, Inventory inventory) {
        return new TransmutationScreen(player.inventory, new TransmutationInventory(player));
    }
}
