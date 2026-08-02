package net.ralf2oo2.projecte.packet;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.ralf2oo2.projecte.api.item.ExtraFunction;
import net.ralf2oo2.projecte.api.item.ItemCharge;
import net.ralf2oo2.projecte.api.item.ModeChanger;
import net.ralf2oo2.projecte.config.Config;
import net.ralf2oo2.projecte.util.ProjectEKeybind;
import net.ralf2oo2.projecte.util.StackUtil;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class KeyPressedC2SPacket extends Packet implements ManagedPacket<KeyPressedC2SPacket> {
    public static final PacketType<KeyPressedC2SPacket> TYPE = PacketType.builder(false, true, KeyPressedC2SPacket::new).build();

    private ProjectEKeybind key;

    public KeyPressedC2SPacket() {
    }

    public KeyPressedC2SPacket(ProjectEKeybind key) {
        this.key = key;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            key = ProjectEKeybind.values()[stream.readByte()];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeByte(key.ordinal());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);

        //TODO: handle armor toggle

        ItemStack stack = player.getHand();
        switch (key) {
            case CHARGE -> {
                if(!StackUtil.isEmpty(stack) && stack.getItem() instanceof ItemCharge itemCharge && itemCharge.changeCharge(player, stack)) {
                    return;
                } else if(Config.MISCELANIOUS_CONFIG.unsafeKeyBinds || StackUtil.isEmpty(stack)) {
                    // TODO: gem state change
                }
            }
            case EXTRA_FUNCTION -> {
                if(!StackUtil.isEmpty(stack) && stack.getItem() instanceof ExtraFunction extraFunction && extraFunction.doExtraFunction(stack, player)) {
                    return;
                } else if(Config.MISCELANIOUS_CONFIG.unsafeKeyBinds || StackUtil.isEmpty(stack)) {
                    // TODO: gem state change
                }
            }
            case FIRE_PROJECTILE -> {
                // TODO: implement case
            }
            case MODE -> {
                if(!StackUtil.isEmpty(stack) && stack.getItem() instanceof ModeChanger modeChanger && modeChanger.changeMode(player, stack)) {
                    return;
                }
            }
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType<KeyPressedC2SPacket> getType() {
        return TYPE;
    }
}
