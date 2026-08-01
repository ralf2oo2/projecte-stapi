package net.ralf2oo2.projecte.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.ralf2oo2.projecte.block.entity.AntiMatterRelayMK3BlockEntity;
import net.ralf2oo2.projecte.screen.handler.AntiMatterRelayMK3ScreenHandler;
import net.ralf2oo2.projecte.util.Constants;
import org.lwjgl.opengl.GL11;

public class AntiMatterRelayMK3Screen extends HandledScreen {
    private static final String texture = "/assets/projecte/stationapi/textures/gui/relay3.png";
    private final AntiMatterRelayMK3BlockEntity blockEntity;
    private final AntiMatterRelayMK3ScreenHandler screenHandler;

    public AntiMatterRelayMK3Screen(PlayerInventory playerInventory, AntiMatterRelayMK3BlockEntity blockEntity) {
        super(new AntiMatterRelayMK3ScreenHandler(playerInventory, blockEntity));
        this.blockEntity = blockEntity;
        this.backgroundWidth = 212;
        this.backgroundHeight = 194;
        this.screenHandler = (AntiMatterRelayMK3ScreenHandler)this.handler;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
    }

    @Override
    protected void drawForeground() {
        this.textRenderer.draw(I18n.getTranslation("pe.relay.mk3"), 38, 6, 4210752);
        this.textRenderer.draw(Constants.EMC_FORMATTER.format(screenHandler.emc), 125, 39, 4210752);
    }

    @Override
    protected void drawBackground(float tickDelta) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.INSTANCE.textureManager.bindTexture(Minecraft.INSTANCE.textureManager.getTextureId(texture));

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        this.drawTexture(x, y, 0, 0, backgroundWidth, backgroundHeight);

        //Emc bar progress
        int progress = (int) ((double) screenHandler.emc / blockEntity.getMaximumEmc() * 102);
        this.drawTexture(x + 105, y + 6, 30, 195, progress, 10);

        //Klein start bar progress. Max is 30.
        progress = (int) (screenHandler.kleinChargeProgress * 30);
        this.drawTexture(x + 153, y + 82, 0, 195, progress, 10);

        //Burn Slot bar progress. Max is 30.
        progress = (int) (screenHandler.inputBurnProgress * 30);
        drawTexture(x + 101, y + 82, 0, 195, progress, 10);
    }
}
