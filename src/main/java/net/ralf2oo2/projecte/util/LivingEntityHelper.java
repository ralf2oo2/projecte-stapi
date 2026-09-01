package net.ralf2oo2.projecte.util;

import net.danygames2014.nyalib.particle.ParticleHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class LivingEntityHelper {
    public static void applyKnockback(LivingEntity target, int amount, double dx, double dz) {
        float var7 = MathHelper.sqrt(dx * dx + dz * dz);
        target.velocityX /= 2.0F;
        target.velocityY /= 2.0F;
        target.velocityZ /= 2.0F;
        target.velocityX -= dx / (double)var7 * (double)amount;
        target.velocityY += 0.4F;
        target.velocityZ -= dz / (double)var7 * (double)amount;
        if (target.velocityY > (double)0.4F) {
            target.velocityY = 0.4F;
        }
    }

    public static void spawnExplosionParticle(LivingEntity entity) {
        for(int i = 0; i < 20; ++i) {
            double d0 = entity.random.nextGaussian() * 0.02;
            double d1 = entity.random.nextGaussian() * 0.02;
            double d2 = entity.random.nextGaussian() * 0.02;
            ParticleHelper.addParticle(entity.world, "explode", entity.x + (double)(entity.random.nextFloat() * entity.width * 2.0F) - (double)entity.width - d0 * (double)10.0F, entity.y + (double)(entity.random.nextFloat() * entity.height) - d1 * (double)10.0F, entity.z + (double)(entity.random.nextFloat() * entity.width * 2.0F) - (double)entity.width - d2 * (double)10.0F, d0, d1, d2);
        }
    }
}
