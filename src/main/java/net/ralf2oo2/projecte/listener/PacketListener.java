package net.ralf2oo2.projecte.listener;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.network.packet.PacketRegisterEvent;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.packet.KeyPressedC2SPacket;
import net.ralf2oo2.projecte.packet.KnowledgeSyncS2CPacket;
import net.ralf2oo2.projecte.packet.SearchUpdateC2SPacket;
import net.ralf2oo2.projecte.packet.SendLongPropertyS2CPacket;

public class PacketListener {

    @EventListener
    public void registerPacketTypes(PacketRegisterEvent event) {
        event.register(ProjectE.NAMESPACE.id("search_update"), SearchUpdateC2SPacket.TYPE);
        event.register(ProjectE.NAMESPACE.id("knowledge_sync"), KnowledgeSyncS2CPacket.TYPE);
        event.register(ProjectE.NAMESPACE.id("send_long_property"), SendLongPropertyS2CPacket.TYPE);
        event.register(ProjectE.NAMESPACE.id("key_pressed"), KeyPressedC2SPacket.TYPE);
    }
}
