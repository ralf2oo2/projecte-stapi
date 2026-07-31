package net.ralf2oo2.projecte.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.ralf2oo2.projecte.block.entity.EnergyCollectorMK3BlockEntity;
import net.ralf2oo2.projecte.screen.handler.EnergyCollectorMK3ScreenHandler;
import net.ralf2oo2.projecte.util.Constants;
import org.lwjgl.opengl.GL11;

public class EnergyCollectorMK3Screen extends HandledScreen {
    private static final String texture = "/assets/projecte/stationapi/textures/gui/collector3.png";
    private final EnergyCollectorMK3BlockEntity blockEntity;
    private final EnergyCollectorMK3ScreenHandler screenHandler;

    public EnergyCollectorMK3Screen(PlayerInventory playerInventory, EnergyCollectorMK3BlockEntity blockEntity) {
        super(new EnergyCollectorMK3ScreenHandler(playerInventory, blockEntity));
        this.screenHandler = (EnergyCollectorMK3ScreenHandler)handler;
        this.blockEntity = blockEntity;
        this.backgroundWidth = 218;
        this.backgroundHeight = 165;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        super.render(mouseX, mouseY, delta);
    }

    @Override
    protected void drawForeground() {
        this.textRenderer.draw(Long.toString(screenHandler.emc), 91, 32, 4210752);

        long kleinCharge = screenHandler.kleinEmc;

        if (kleinCharge > 0)
            this.textRenderer.draw(Constants.EMC_FORMATTER.format(kleinCharge), 91, 44, 4210752);
    }

    @Override
    protected void drawBackground(float tickDelta) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.INSTANCE.textureManager.bindTexture(Minecraft.INSTANCE.textureManager.getTextureId(texture));

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        this.drawTexture(x, y, 0, 0, backgroundWidth, backgroundHeight);

        //Light Level. Max is 12
        int progress = (int) (screenHandler.sunLevel * 12.0 / 16);

        this.drawTexture(x + 160, y + 49 - progress, 220, 13 - progress, 12, progress);

        //EMC storage. Max is 48
        this.drawTexture(x + 98, y + 18, 0, 166, (int) (screenHandler.emc / blockEntity.getMaximumEmc() * 48), 10);

        //Klein Star Charge Progress. Max is 48
        progress = (int) (screenHandler.kleinChargeProgress * 48);
        this.drawTexture(x + 98, y + 58, 0, 166, progress, 10);

        //Fuel Progress. Max is 24.
        progress = (int) (screenHandler.fuelProgress * 24);
        this.drawTexture(x + 172, y + 55 - progress, 219, 38 - progress, 10, progress + 1);
    }
}
