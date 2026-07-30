package net.ralf2oo2.projecte.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.impl.item.StationNBTSetter;
import net.ralf2oo2.projecte.screen.handler.TransmutationScreenHandler;
import net.ralf2oo2.projecte.util.SendNbtCompound;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SearchUpdateC2SPacket extends Packet implements ManagedPacket<SearchUpdateC2SPacket> {
    public static final PacketType<SearchUpdateC2SPacket> TYPE = PacketType.builder(false, true, SearchUpdateC2SPacket::new).build();

    public int slot;
    public ItemStack stack;

    public SearchUpdateC2SPacket(int slot, ItemStack stack) {
        this.slot = slot;
        this.stack = stack;
    }

    public SearchUpdateC2SPacket(){}

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeInt(slot);
            if(stack != null) {
                stream.writeInt(stack.itemId);
                stream.writeInt(stack.count);
                stream.writeInt(stack.getDamage());
                SendNbtCompound.writeNbtCompound(stack.getStationNbt(), stream);
            } else {
                stream.writeInt(0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void read(DataInputStream stream) {
        try {

            this.slot = stream.readInt();
            final int itemID = stream.readInt();

            if(itemID != 0) {
                stack = new ItemStack(itemID, stream.readInt(), stream.readInt());
                StationNBTSetter.cast(stack).setStationNbt(SendNbtCompound.readNbtCompound(stream));
            } else {
                stack = null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity playerEntity = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(playerEntity.currentScreenHandler instanceof TransmutationScreenHandler screenHandler) {
            screenHandler.transmutationInventory.writeIntoOutputSlot(slot, stack);
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType<SearchUpdateC2SPacket> getType() {
        return TYPE;
    }
}
