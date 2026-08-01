package net.ralf2oo2.projecte.client.render.entity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import org.lwjgl.opengl.GL11;

public class PrimedNovaEntityRenderer extends EntityRenderer {
    private final Block block;

    public PrimedNovaEntityRenderer(Block block) {
        this.shadowRadius = 0.5F;
        this.block = block;
    }

    public void render(Entity entity, double x, double y, double z, float yaw, float pitch) {
        BlockRenderManager blockRenderManager = Minecraft.INSTANCE.worldRenderer.blockRenderManager;
        TntEntity tntEntity = (TntEntity) entity;

        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        if ((float)tntEntity.fuse - pitch + 1.0F < 10.0F) {
            float var10 = 1.0F - ((float)tntEntity.fuse - pitch + 1.0F) / 10.0F;
            if (var10 < 0.0F) {
                var10 = 0.0F;
            }

            if (var10 > 1.0F) {
                var10 = 1.0F;
            }

            var10 *= var10;
            var10 *= var10;
            float var11 = 1.0F + var10 * 0.3F;
            GL11.glScalef(var11, var11, var11);
        }

        float var14 = (1.0F - ((float)tntEntity.fuse - pitch + 1.0F) / 100.0F) * 0.8F;
        StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture();
        blockRenderManager.render(block, 0, tntEntity.getBrightnessAtEyes(pitch));
        if (tntEntity.fuse / 5 % 2 == 0) {
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 772);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, var14);
            blockRenderManager.render(block, 0, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
        }

        GL11.glPopMatrix();
    }
}
