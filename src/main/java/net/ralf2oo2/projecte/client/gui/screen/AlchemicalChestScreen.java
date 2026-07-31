package net.ralf2oo2.projecte.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.ralf2oo2.projecte.block.entity.AlchemicalChestBlockEntity;
import net.ralf2oo2.projecte.screen.handler.AlchemicalBagScreenHandler;
import net.ralf2oo2.projecte.screen.handler.AlchemicalChestScreenHandler;
import org.lwjgl.opengl.GL11;

public class AlchemicalChestScreen extends HandledScreen {
    private static final String texture = "/assets/projecte/stationapi/textures/gui/alchchest.png";

    public AlchemicalChestScreen(PlayerInventory playerInventory, Inventory alchemicalBagInventory) {
        super(new AlchemicalBagScreenHandler(playerInventory, alchemicalBagInventory));
        this.backgroundWidth = 255;
        this.backgroundHeight = 230;
    }

    public AlchemicalChestScreen(PlayerInventory playerInventory, AlchemicalChestBlockEntity blockEntity) {
        super(new AlchemicalChestScreenHandler(playerInventory, blockEntity));
        this.backgroundWidth = 255;
        this.backgroundHeight = 230;
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

        int guiLeft = (this.width - this.backgroundWidth) / 2;
        int guiTop = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(guiLeft, guiTop, 0, 0, backgroundWidth, backgroundHeight);
    }


}
