package net.ralf2oo2.projecte.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.ralf2oo2.projecte.block.entity.DarkMatterFurnaceBlockEntity;
import net.ralf2oo2.projecte.block.entity.RedMatterFurnaceBlockEntity;
import net.ralf2oo2.projecte.screen.handler.DarkMatterFurnaceScreenHandler;
import net.ralf2oo2.projecte.screen.handler.RedMatterFurnaceScreenHandler;
import org.lwjgl.opengl.GL11;

public class DarkMatterFurnaceScreen extends HandledScreen {
    private static final String texture = "/assets/projecte/stationapi/textures/gui/dmfurnace.png";
    private final DarkMatterFurnaceBlockEntity blockEntity;

    public DarkMatterFurnaceScreen(PlayerInventory playerInventory, DarkMatterFurnaceBlockEntity blockEntity) {
        super(new DarkMatterFurnaceScreenHandler(playerInventory, blockEntity));
        this.backgroundWidth = 178;
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
            this.drawTexture(x + 49, y + 36 + 12 - progress, 179, 12 - progress, 14, progress + 2);
        }
        progress = blockEntity.getCookProgressScaled(24);
        this.drawTexture(x + 73, y + 34, 179, 14, progress + 1, 16);
    }

    @Override
    protected void drawForeground() {
        this.textRenderer.draw(I18n.getTranslation("pe.dmfurnace.shortname"), 57, 5, 4210752);
        this.textRenderer.draw(I18n.getTranslation("container.inventory"), 57, backgroundHeight - 96 + 2, 4210752);
    }
}
