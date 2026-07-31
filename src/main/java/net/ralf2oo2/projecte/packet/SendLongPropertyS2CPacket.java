package net.ralf2oo2.projecte.packet;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.ralf2oo2.projecte.screen.handler.LongScreenHandler;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SendLongPropertyS2CPacket extends Packet implements ManagedPacket<SendLongPropertyS2CPacket> {
    public static final PacketType<SendLongPropertyS2CPacket> TYPE = PacketType.builder(true, false, SendLongPropertyS2CPacket::new).build();

    private int syncId;
    private int propertyId;
    private long propertyValue;

    public SendLongPropertyS2CPacket(int syncId, int propertyId, long propertyValue) {
        this.syncId = syncId;
        this.propertyId = propertyId;
        this.propertyValue = propertyValue;
    }

    public SendLongPropertyS2CPacket() {
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            this.syncId = stream.readInt();
            this.propertyId = stream.readInt();
            this.propertyValue = stream.readLong();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeInt(this.syncId);
            stream.writeInt(this.propertyId);
            stream.writeLong(this.propertyValue);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(player.currentScreenHandler != null && player.currentScreenHandler.syncId == syncId) {
            //It should always be a LongContainer if it is this type of packet, if not fallback to normal update
            if(player.currentScreenHandler instanceof LongScreenHandler screenHandler) {
                screenHandler.setLongProperty(propertyId, propertyValue);
            } else {
                player.currentScreenHandler.setProperty(propertyId, (int) propertyValue);
            }
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType<SendLongPropertyS2CPacket> getType() {
        return TYPE;
    }
}
