package net.ralf2oo2.projecte.listener;

import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.server.event.network.PlayerLoginEvent;
import net.ralf2oo2.projecte.api.capability.KnowledgeEntityCapability;

public class PlayerLoginListener {
    @EventListener
    public void onPlayerLogin(PlayerLoginEvent event) {
        KnowledgeEntityCapability capability = CapabilityHelper.getCapability(event.player, KnowledgeEntityCapability.class);
        if(capability != null) {
            capability.sync(event.player);
        }
    }
}
