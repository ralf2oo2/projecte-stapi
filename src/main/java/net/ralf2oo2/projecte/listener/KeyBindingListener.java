package net.ralf2oo2.projecte.listener;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.option.KeyBinding;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent;
import net.ralf2oo2.projecte.util.ProjectEKeybind;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.Map;

public class KeyBindingListener {

    public static Map<ProjectEKeybind, KeyBinding> MC_TO_PE = new HashMap<>();

    public static KeyBinding armorToggle;
    public static KeyBinding charge;
    public static KeyBinding extraFunction;
    public static KeyBinding fireProjectile;
    public static KeyBinding mode;

    @EventListener
    public void registerKeyBindings(KeyBindingRegisterEvent event) {
        event.register(armorToggle = createrKeyBinding(ProjectEKeybind.ARMOR_TOGGLE));
        event.register(charge = createrKeyBinding(ProjectEKeybind.CHARGE));
        event.register(extraFunction = createrKeyBinding(ProjectEKeybind.EXTRA_FUNCTION));
        event.register(fireProjectile = createrKeyBinding(ProjectEKeybind.FIRE_PROJECTILE));
        event.register(mode = createrKeyBinding(ProjectEKeybind.MODE));
    }

    private KeyBinding createrKeyBinding(ProjectEKeybind keybind) {
        KeyBinding keyBinding = new KeyBinding(keybind.keyName, keybind.defaultKeyCode);
        MC_TO_PE.put(keybind, keyBinding);
        return keyBinding;
    }
}
