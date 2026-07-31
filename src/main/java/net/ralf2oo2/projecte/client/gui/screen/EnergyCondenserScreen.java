package net.ralf2oo2.projecte.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.ralf2oo2.projecte.block.entity.EnergyCondenserBlockEntity;
import net.ralf2oo2.projecte.screen.handler.EnergyCondenserScreenHandler;
import net.ralf2oo2.projecte.util.TransmutationEMCFormatter;
import org.lwjgl.opengl.GL11;

public class EnergyCondenserScreen extends HandledScreen {

    public static String MK1_BACKGROUND = "/assets/projecte/stationapi/textures/gui/condenser.png";
    public static String MK2_BACKGROUND = "/assets/projecte/stationapi/textures/gui/condenser_mk2.png";

    protected final String texture;
    protected final EnergyCondenserScreenHandler screenHandler;

    public EnergyCondenserScreen(EnergyCondenserScreenHandler screenHandler, String texture) {
        super(screenHandler);
        this.screenHandler = screenHandler;
        this.texture = texture;
        this.backgroundWidth = 255;
        this.backgroundHeight = 233;
    }

    public EnergyCondenserScreen(PlayerInventory playerInventory, EnergyCondenserBlockEntity blockEntity) {
        this(new EnergyCondenserScreenHandler(playerInventory, blockEntity), MK1_BACKGROUND);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        super.render(mouseX, mouseY, delta);
    }

    @Override
    protected void drawBackground(float tickDelta) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.INSTANCE.textureManager.bindTexture(Minecraft.INSTANCE.textureManager.getTextureId(texture));

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        this.drawTexture(x, y, 0, 0, backgroundWidth, backgroundHeight);

        int progress = screenHandler.getProgressScaled();
        this.drawTexture(x + 33, y + 10, 0, 235, progress, 10);
    }

    @Override
    protected void drawForeground() {
        long toDisplay = Math.min(screenHandler.displayEmc, screenHandler.requiredEmc);
        String emc = TransmutationEMCFormatter.EMCFormat(toDisplay);
        this.textRenderer.draw(emc, 140, 10, 4210752);
    }
}
