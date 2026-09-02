package net.ralf2oo2.projecte.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.math.MathHelper;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.render.RendererAccess;
import net.modificationstation.stationapi.api.client.render.model.BakedModelRenderer;
import net.modificationstation.stationapi.api.client.render.model.json.ModelTransformation;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.ralf2oo2.projecte.block.entity.DarkMatterPedestalBlockEntity;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

public class DarkMatterPedestalBlockEntityRenderer extends BlockEntityRenderer {
    @Override
    public void render(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {
        DarkMatterPedestalBlockEntity pedestal = (DarkMatterPedestalBlockEntity)blockEntity;
        if(pedestal.getInventory().getStack(0) == null) {
            return;
        }
        GL11.glPushMatrix();
        BakedModelRenderer renderer = RendererAccess.INSTANCE.getRenderer().bakedModelRenderer();
        GL11.glTranslated(x + 0.5, y + 0.825, z + 0.5);
        GL11.glTranslated(0, Math.sin((blockEntity.world.getTime() + tickDelta) / 10.0F) * 0.1F + 0.1F, 0);
        GL11.glScaled(0.40, 0.40, 0.40);
        float angle = (blockEntity.world.getTime() + tickDelta) / 20.0F * (180F / (float)Math.PI);
        GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
        GL11.glColor3f(1f, 1f, 1f);
        StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture();
        Tessellator.INSTANCE.startQuads();
        renderer.renderItem(pedestal.getInventory().getStack(0), ModelTransformation.Mode.NONE, blockEntity.world.dimension.lightLevelToLuminance[blockEntity.world.getBrightness(blockEntity.x, blockEntity.y, blockEntity.z)], renderer.getModel(pedestal.getInventory().getStack(0), null, null, Objects.hash(x, y, z) + ModelTransformation.Mode.NONE.ordinal()));
        Tessellator.INSTANCE.draw();
        GL11.glPopMatrix();
    }
}
