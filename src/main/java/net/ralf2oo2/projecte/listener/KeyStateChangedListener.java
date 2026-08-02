package net.ralf2oo2.projecte.listener;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.option.KeyBinding;
import net.modificationstation.stationapi.api.client.event.keyboard.KeyStateChangedEvent;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.ralf2oo2.projecte.packet.KeyPressedC2SPacket;
import net.ralf2oo2.projecte.util.ProjectEKeybind;
import org.lwjgl.input.Keyboard;

public class KeyStateChangedListener {

    @EventListener
    public void onKeyStateChanged(KeyStateChangedEvent event) {
        if(event.environment == KeyStateChangedEvent.Environment.IN_GAME) {
            for(ProjectEKeybind peKeybind : ProjectEKeybind.values()) {
                KeyBinding mcKeybind = KeyBindingListener.MC_TO_PE.get(peKeybind);
                if(Keyboard.getEventKeyState() && Keyboard.getEventKey() == mcKeybind.code) {
                    PacketHelper.send(new KeyPressedC2SPacket(peKeybind));
                }
            }
        }
    }
}
