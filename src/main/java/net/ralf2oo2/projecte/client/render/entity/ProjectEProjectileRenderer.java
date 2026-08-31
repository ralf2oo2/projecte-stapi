package net.ralf2oo2.projecte.client.render.entity;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ProjectEProjectileRenderer extends EntityRenderer {
    @Override
    public void render(Entity entity, double x, double y, double z, float yaw, float pitch) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glEnable(32826);
        GL11.glScalef(0.5F, 0.5F, 0.5F);

        this.bindTexture(entity.getTexture());

        Tessellator var10 = Tessellator.INSTANCE;

        float minU = 0.0F;
        float maxU = 1.0F;
        float minV = 0.0F;
        float maxV = 1.0F;

        float var15 = 1.0F;
        float var16 = 0.5F;
        float var17 = 0.25F;

        GL11.glRotatef(180.0F - this.dispatcher.yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.dispatcher.pitch, 1.0F, 0.0F, 0.0F);

        var10.startQuads();
        var10.normal(0.0F, 1.0F, 0.0F);
        var10.vertex(0.0F - var16, 0.0F - var17, 0.0D, minU, maxV);
        var10.vertex(var15 - var16, 0.0F - var17, 0.0D, maxU, maxV);
        var10.vertex(var15 - var16, 1.0F - var17, 0.0D, maxU, minV);
        var10.vertex(0.0F - var16, 1.0F - var17, 0.0D, minU, minV);
        var10.draw();

        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }
}
