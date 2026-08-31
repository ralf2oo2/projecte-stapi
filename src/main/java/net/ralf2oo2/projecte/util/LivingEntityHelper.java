package net.ralf2oo2.projecte.util;

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
}
