package net.ralf2oo2.projecte.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class BoxHelper {
    public static Box createFromCornersCached(BlockPos pos1, BlockPos pos2) {
        double minX = Math.min(pos1.x, pos2.x);
        double minY = Math.min(pos1.y, pos2.y);
        double minZ = Math.min(pos1.z, pos2.z);

        double maxX = Math.max(pos1.x, pos2.x) + 1.0;
        double maxY = Math.max(pos1.y, pos2.y) + 1.0;
        double maxZ = Math.max(pos1.z, pos2.z) + 1.0;

        return Box.createCached(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static Box createFromCorners(BlockPos pos1, BlockPos pos2) {
        double minX = Math.min(pos1.x, pos2.x);
        double minY = Math.min(pos1.y, pos2.y);
        double minZ = Math.min(pos1.z, pos2.z);

        double maxX = Math.max(pos1.x, pos2.x) + 1.0;
        double maxY = Math.max(pos1.y, pos2.y) + 1.0;
        double maxZ = Math.max(pos1.z, pos2.z) + 1.0;

        return Box.create(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
