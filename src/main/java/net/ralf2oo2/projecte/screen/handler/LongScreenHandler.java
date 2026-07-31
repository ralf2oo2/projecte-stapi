package net.ralf2oo2.projecte.screen.handler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.screen.ScreenHandler;

public abstract class LongScreenHandler extends ScreenHandler {
    @Environment(EnvType.CLIENT)
    public void setLongProperty(int id, long data) {
    }
}
