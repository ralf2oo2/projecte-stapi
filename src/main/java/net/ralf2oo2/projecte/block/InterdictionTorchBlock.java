package net.ralf2oo2.projecte.block;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.DirectionProperty;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.ralf2oo2.projecte.block.entity.InterdictionTorchBlockEntity;

import java.util.Random;

public class InterdictionTorchBlock extends TemplateBlockWithEntity {
    public static DirectionProperty FACING = DirectionProperty.of("facing", (direction) -> direction != Direction.DOWN);

    public InterdictionTorchBlock(Identifier identifier) {
        super(identifier, Material.PISTON_BREAKABLE);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.UP));
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        Direction facing = world.getBlockState(x, y, z).get(FACING);
        double d0 = (double)x + 0.5D;
        double d1 = (double)y + 0.7D;
        double d2 = (double)z + 0.5D;
        double d3 = 0.22D;
        double d4 = 0.27D;

        if (facing.getAxis().isHorizontal())
        {
            Direction opposite = facing.getOpposite();
            world.addParticle("smoke", d0 + d4 * (double)opposite.getOffsetZ(), d1 + d3, d2 + d4 * (double)opposite.getOffsetZ(), 0.0D, 0.0D, 0.0D);
        }
        else
        {
            world.addParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        Direction side = context.getSide();

        if (side == Direction.UP && world.shouldSuffocate(pos.x, pos.y - 1, pos.z)) {
            return this.getDefaultState().with(FACING, Direction.UP);
        }

        if (side == Direction.NORTH && world.shouldSuffocate(pos.x, pos.y, pos.z + 1)) {
            return this.getDefaultState().with(FACING, Direction.NORTH);
        }

        if (side == Direction.SOUTH && world.shouldSuffocate(pos.x, pos.y, pos.z - 1)) {
            return this.getDefaultState().with(FACING, Direction.SOUTH);
        }

        if (side == Direction.WEST && world.shouldSuffocate(pos.x + 1, pos.y, pos.z)) {
            return this.getDefaultState().with(FACING, Direction.WEST);
        }

        if (side == Direction.EAST && world.shouldSuffocate(pos.x - 1, pos.y, pos.z)) {
            return this.getDefaultState().with(FACING, Direction.EAST);
        }

        return this.getDefaultState();
    }

    public boolean breakIfCannotPlaceAt(World world, BlockPos pos, BlockState state) {
        if (!this.canPlaceAt(world, pos.x, pos.y, pos.z)) {
            this.dropStacks(world, pos.x, pos.y, pos.z, world.getBlockMeta(pos.x, pos.y, pos.z));
            world.setBlock(pos.x, pos.y, pos.z, 0);
            return true;
        }
        return false;
    }

    @Override
    public void neighborUpdate(World world, int x, int y, int z, int id) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = world.getBlockState(x, y, z);

        if (this.breakIfCannotPlaceAt(world, pos, state)) {
            return;
        }

        Direction facing = state.get(FACING);
        boolean shouldBreak = false;

        if (facing == Direction.EAST && !world.shouldSuffocate(x - 1, y, z)) {
            shouldBreak = true;
        } else if (facing == Direction.WEST && !world.shouldSuffocate(x + 1, y, z)) {
            shouldBreak = true;
        } else if (facing == Direction.SOUTH && !world.shouldSuffocate(x, y, z - 1)) {
            shouldBreak = true;
        } else if (facing == Direction.NORTH && !world.shouldSuffocate(x, y, z + 1)) {
            shouldBreak = true;
        } else if (facing == Direction.UP && !world.shouldSuffocate(x, y - 1, z)) {
            shouldBreak = true;
        }

        if (shouldBreak) {
            this.dropStacks(world, x, y, z, world.getBlockMeta(x, y, z));
            world.setBlock(x, y, z, 0);
        }
    }

    public HitResult raycast(World world, int x, int y, int z, Vec3d startPos, Vec3d endPos) {
        Direction facing = world.getBlockState(x, y, z).get(FACING);
        float var8 = 0.15F;
        if (facing == Direction.EAST) {
            this.setBoundingBox(0.0F, 0.2F, 0.5F - var8, var8 * 2.0F, 0.8F, 0.5F + var8);
        } else if (facing == Direction.WEST) {
            this.setBoundingBox(1.0F - var8 * 2.0F, 0.2F, 0.5F - var8, 1.0F, 0.8F, 0.5F + var8);
        } else if (facing == Direction.SOUTH) {
            this.setBoundingBox(0.5F - var8, 0.2F, 0.0F, 0.5F + var8, 0.8F, var8 * 2.0F);
        } else if (facing == Direction.NORTH) {
            this.setBoundingBox(0.5F - var8, 0.2F, 1.0F - var8 * 2.0F, 0.5F + var8, 0.8F, 1.0F);
        } else {
            var8 = 0.1F;
            this.setBoundingBox(0.5F - var8, 0.0F, 0.5F - var8, 0.5F + var8, 0.6F, 0.5F + var8);
        }

        return super.raycast(world, x, y, z, startPos, endPos);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new InterdictionTorchBlockEntity();
    }

    @Override
    public Box getCollisionShape(World world, int x, int y, int z) {
        return null;
    }
}
