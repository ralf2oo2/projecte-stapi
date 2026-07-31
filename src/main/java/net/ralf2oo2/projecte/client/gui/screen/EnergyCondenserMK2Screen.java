package net.ralf2oo2.projecte.client.gui.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.ralf2oo2.projecte.block.entity.EnergyCondenserMK2BlockEntity;
import net.ralf2oo2.projecte.screen.handler.EnergyCondenserMK2ScreenHandler;

public class EnergyCondenserMK2Screen extends EnergyCondenserScreen {
    public EnergyCondenserMK2Screen(PlayerInventory playerInventory, EnergyCondenserMK2BlockEntity blockEntity) {
        super(new EnergyCondenserMK2ScreenHandler(playerInventory, blockEntity), EnergyCondenserScreen.MK2_BACKGROUND);
    }
}
