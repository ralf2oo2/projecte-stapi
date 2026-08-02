package net.ralf2oo2.projecte.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.modificationstation.stationapi.api.util.math.MutableBlockPos;
import net.ralf2oo2.projecte.util.WorldHelper;

public class InterdictionTorchBlockEntity extends BlockEntity {

    MutableBlockPos pos = new MutableBlockPos();

    @Override
    public void tick() {
        pos.set(x, y, z);
        BlockPos min = pos.add(-8, -8, -8);
        BlockPos max = pos.add(8, 8, 8);

        int minX = Math.min(min.x, max.x);
        int minY = Math.min(min.y, max.y);
        int minZ = Math.min(min.z, max.z);

        int maxX = Math.max(min.x, max.x) + 1;
        int maxY = Math.max(min.y, max.y) + 1;
        int maxZ = Math.max(min.z, max.z) + 1;

        WorldHelper.repelEntitiesInAABBFromPoint(world, Box.create(minX, minY, minZ, maxX, maxY, maxZ), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, false);
    }
}
