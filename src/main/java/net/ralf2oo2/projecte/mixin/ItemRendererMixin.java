package net.ralf2oo2.projecte.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.ralf2oo2.projecte.api.item.ItemWithDisplayDurability;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow
    protected abstract void fillRect(Tessellator tessellator, int x, int y, int width, int height, int color);

    @Inject(method = "renderGuiItemDecoration", at = @At("HEAD"))
    public void projecte_renderEmcBar(TextRenderer textrenderer, TextureManager textureManager, ItemStack stack, int x, int y, CallbackInfo ci) {
        if(stack != null && stack.getItem() instanceof ItemWithDisplayDurability displayDurability) {
            double normalized = 1.0 - Math.max(0.0, Math.min(1.0, displayDurability.getDurabilityForDisplay(stack)));

            int var11 = (int) Math.round(normalized * 13.0);
            int var7 = (int) Math.round(normalized * 255.0);

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_TEXTURE_2D);

            Tessellator tessellator = Tessellator.INSTANCE;
            int var9 = (255 - var7) << 16 | var7 << 8;
            int var10 = ((255 - var7) / 4) << 16 | 16128;

            this.fillRect(tessellator, x + 2, y + 13, 13, 2, 0);
            this.fillRect(tessellator, x + 2, y + 13, 12, 1, var10);
            this.fillRect(tessellator, x + 2, y + 13, var11, 1, var9);

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
