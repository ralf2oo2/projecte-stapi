package net.ralf2oo2.projecte.packet;

import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import net.ralf2oo2.projecte.api.capability.KnowledgeEntityCapability;
import net.ralf2oo2.projecte.util.SendNbtCompound;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class KnowledgeSyncS2CPacket extends Packet implements ManagedPacket<KnowledgeSyncS2CPacket> {
    public static final PacketType<KnowledgeSyncS2CPacket> TYPE = PacketType.builder(true, false, KnowledgeSyncS2CPacket::new).build();

    private NbtCompound nbt;

    public KnowledgeSyncS2CPacket(NbtCompound nbt) {
        this.nbt = nbt;
    }

    public KnowledgeSyncS2CPacket() {
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            nbt = SendNbtCompound.readNbtCompound(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            SendNbtCompound.writeNbtCompound(nbt, stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        SideUtil.run(() -> {
            KnowledgeEntityCapability capability = CapabilityHelper.getCapability(PlayerHelper.getPlayerFromPacketHandler(networkHandler), KnowledgeEntityCapability.class);
            if(capability != null) {
                capability.readNbt(nbt);
            }
        }, () -> {});
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType<KnowledgeSyncS2CPacket> getType() {
        return TYPE;
    }
}
