package net.ralf2oo2.projecte.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.ralf2oo2.projecte.config.Config;
import net.ralf2oo2.projecte.screen.handler.TransmutationScreenHandler;
import net.ralf2oo2.projecte.screen.inventory.TransmutationInventory;
import net.ralf2oo2.projecte.util.TransmutationEMCFormatter;
import org.lwjgl.opengl.GL11;

import java.util.Locale;

public class TransmutationScreen extends HandledScreen {
    private static final String texture = "/assets/projecte/stationapi/textures/gui/transmute.png";
    private final TransmutationInventory inv;
    private TextFieldWidget textBoxFilter;

    public TransmutationScreen(PlayerInventory playerInventory, TransmutationInventory inventory) {
        super(new TransmutationScreenHandler(playerInventory, inventory));
        this.inv = inventory;
        this.backgroundWidth = 228;
        this.backgroundHeight = 196;
    }

    @Override
    public void init() {
        super.init();

        int xLocation = (this.width - this.backgroundWidth) / 2;
        int yLocation = (this.height - this.backgroundHeight) / 2;

        this.textBoxFilter = new TextFieldWidget(this, this.textRenderer, xLocation + 88, yLocation + 8, 45, 10, "");
        this.textBoxFilter.setText(inv.filter);

        if(!Config.DIFFICULTY_CONFIG.disableTransmutationSearch) {
            this.buttons.add(new ButtonWidget(1, xLocation + 125, yLocation + 100, 14, 14, "<"));
            this.buttons.add(new ButtonWidget(2, xLocation + 193, yLocation + 100, 14, 14, ">"));
        }
    }

    @Override
    protected void drawBackground(float tickDelta) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.INSTANCE.textureManager.bindTexture(Minecraft.INSTANCE.textureManager.getTextureId(texture));

        int guiLeft = (this.width - this.backgroundWidth) / 2;
        int guiTop = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(guiLeft, guiTop, 0, 0, backgroundWidth, backgroundHeight);

        if(!Config.DIFFICULTY_CONFIG.disableTransmutationSearch) {
            this.textBoxFilter.render();
        }
    }

    @Override
    protected void drawForeground() {
        this.textRenderer.draw(I18n.getTranslation("pe.transmutation.transmute"), 6, 5, 4210752);
        long emcAmount = inv.getAvailableEMC();
        String emcLabel = I18n.getTranslation("pe.emc.emc_tooltip_prefix");
        this.textRenderer.draw(emcLabel, 6, this.backgroundHeight - 100, 4210752);
        String emc = TransmutationEMCFormatter.EMCFormat(emcAmount);
        this.textRenderer.draw(emc, 6, this.backgroundHeight - 90, 4210752);

        if (inv.learnFlag > 0)
        {
            this.textRenderer.draw(I18n.getTranslation("pe.transmutation.learned0"), 98, 30, 4210752);
            this.textRenderer.draw(I18n.getTranslation("pe.transmutation.learned1"), 99, 38, 4210752);
            this.textRenderer.draw(I18n.getTranslation("pe.transmutation.learned2"), 100, 46, 4210752);
            this.textRenderer.draw(I18n.getTranslation("pe.transmutation.learned3"), 101, 54, 4210752);
            this.textRenderer.draw(I18n.getTranslation("pe.transmutation.learned4"), 102, 62, 4210752);
            this.textRenderer.draw(I18n.getTranslation("pe.transmutation.learned5"), 103, 70, 4210752);
            this.textRenderer.draw(I18n.getTranslation("pe.transmutation.learned6"), 104, 78, 4210752);
            this.textRenderer.draw(I18n.getTranslation("pe.transmutation.learned7"), 107, 86, 4210752);

            inv.learnFlag--;
        }

        if (inv.unlearnFlag > 0)
        {
            this.textRenderer.draw(I18n.getTranslation("pe.transmutation.unlearned0"), 97, 22, 4210752);
            this.textRenderer.draw(I18n.getTranslation("pe.transmutation.unlearned1"), 98, 30, 4210752);
            this.textRenderer.draw(I18n.getTranslation("pe.transmutation.unlearned2"), 99, 38, 4210752);
            this.textRenderer.draw(I18n.getTranslation("pe.transmutation.unlearned3"), 100, 46, 4210752);
            this.textRenderer.draw(I18n.getTranslation("pe.transmutation.unlearned4"), 101, 54, 4210752);
            this.textRenderer.draw(I18n.getTranslation("pe.transmutation.unlearned5"), 102, 62, 4210752);
            this.textRenderer.draw(I18n.getTranslation("pe.transmutation.unlearned6"), 103, 70, 4210752);
            this.textRenderer.draw(I18n.getTranslation("pe.transmutation.unlearned7"), 104, 78, 4210752);
            this.textRenderer.draw(I18n.getTranslation("pe.transmutation.unlearned8"), 107, 86, 4210752);

            inv.unlearnFlag--;
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.textBoxFilter.tick();
    }

    @Override
    protected void keyPressed(char character, int keyCode) {
        if (this.textBoxFilter.focused && !Config.DIFFICULTY_CONFIG.disableTransmutationSearch)
        {
            this.textBoxFilter.keyPressed(character, keyCode);

            String srch = this.textBoxFilter.getText().toLowerCase();

            if (!inv.filter.equals(srch))
            {
                inv.filter = srch;
                inv.searchpage = 0;
                inv.updateClientTargets();
            }
        }

        if (keyCode == 1 || keyCode == this.minecraft.options.inventoryKey.code && !this.textBoxFilter.focused)
        {
            this.minecraft.setScreen(null);
        }
        if(!this.textBoxFilter.focused) {
            super.keyPressed(character, keyCode);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        super.render(mouseX, mouseY, delta);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if(!Config.DIFFICULTY_CONFIG.disableTransmutationSearch) {
            textBoxFilter.mouseClicked(mouseX, mouseY, button);
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        String srch = this.textBoxFilter.getText().toLowerCase(Locale.ROOT);

        if (button.id == 1)
        {
            if (inv.searchpage != 0)
            {
                inv.searchpage--;
            }
        }
        else if (button.id == 2)
        {
            if (!(inv.knowledge.size() <= 12))
            {
                inv.searchpage++;
            }
        }
        inv.filter = srch;
        inv.updateClientTargets();
    }
}
