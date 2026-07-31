package net.ralf2oo2.projecte.util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.ralf2oo2.projecte.packet.SendLongPropertyS2CPacket;

public class PacketUtil {
    public static void sendLongPropertyUpdate(ScreenHandlerListener listener, ScreenHandler screenHandler, int propertyId, long propertyValue) {
        if(listener instanceof ServerPlayerEntity player) {
            PacketHelper.sendTo(player, new SendLongPropertyS2CPacket(screenHandler.syncId, propertyId, propertyValue));
        }
    }
}
