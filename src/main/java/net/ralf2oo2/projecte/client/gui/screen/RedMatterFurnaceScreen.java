package net.ralf2oo2.projecte.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.ralf2oo2.projecte.block.entity.RedMatterFurnaceBlockEntity;
import net.ralf2oo2.projecte.screen.handler.RedMatterFurnaceScreenHandler;
import org.lwjgl.opengl.GL11;

public class RedMatterFurnaceScreen extends HandledScreen {
    private static final String texture = "/assets/projecte/stationapi/textures/gui/rmfurnace.png";
    private final RedMatterFurnaceBlockEntity blockEntity;

    public RedMatterFurnaceScreen(PlayerInventory playerInventory, RedMatterFurnaceBlockEntity blockEntity) {
        super(new RedMatterFurnaceScreenHandler(playerInventory, blockEntity));
        this.backgroundWidth = 209;
        this.backgroundHeight = 165;
        this.blockEntity = blockEntity;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
    }

    @Override
    protected void drawBackground(float tickDelta) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.INSTANCE.textureManager.bindTexture(Minecraft.INSTANCE.textureManager.getTextureId(texture));

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        this.drawTexture(x, y, 0, 0, backgroundWidth, backgroundHeight);

        int progress;

        if (blockEntity.isBurning())
        {
            progress = blockEntity.getBurnTimeRemainingScaled(12);
            this.drawTexture(x + 66, y + 38 + 10 - progress, 210, 10 - progress, 21, progress + 2);
        }

        progress = blockEntity.getCookProgressScaled(24);
        this.drawTexture(x + 88, y + 35, 210, 14, progress, 17);
    }

    @Override
    protected void drawForeground() {
        this.textRenderer.draw(I18n.getTranslation("pe.rmfurnace.shortname"), 76, 5, 4210752);
        this.textRenderer.draw(I18n.getTranslation("container.inventory"), 76, backgroundHeight - 96 + 2, 4210752);
    }
}
