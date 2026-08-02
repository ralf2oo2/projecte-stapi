package net.ralf2oo2.projecte.util;

import org.lwjgl.input.Keyboard;

public enum ProjectEKeybind {
    ARMOR_TOGGLE("pe.key.armor_toggle", Keyboard.KEY_X),
    CHARGE("pe.key.charge", Keyboard.KEY_V),
    EXTRA_FUNCTION("pe.key.extra_function", Keyboard.KEY_C),
    FIRE_PROJECTILE("pe.key.fire_projectile", Keyboard.KEY_R),
    MODE("pe.key.mode", Keyboard.KEY_G);


    public final String keyName;
    public final int defaultKeyCode;

    ProjectEKeybind(String keyName, int defaultKeyCode)
    {
        this.keyName = keyName;
        this.defaultKeyCode = defaultKeyCode;
    }
}
