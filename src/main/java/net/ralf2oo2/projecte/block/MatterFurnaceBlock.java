package net.ralf2oo2.projecte.block;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.block.entity.DarkMatterFurnaceBlockEntity;
import net.ralf2oo2.projecte.block.entity.EnergyCollectorMK1BlockEntity;
import net.ralf2oo2.projecte.block.entity.RedMatterFurnaceBlockEntity;
import net.ralf2oo2.projecte.screen.handler.DarkMatterFurnaceScreenHandler;
import net.ralf2oo2.projecte.screen.handler.EnergyCollectorMK1ScreenHandler;
import net.ralf2oo2.projecte.screen.handler.RedMatterFurnaceScreenHandler;

import java.util.Random;

public class MatterFurnaceBlock extends DirectionalBlock{
    public boolean redMatter;
    private static boolean ignoreBlockRemoval = false;
    public MatterFurnaceBlock(Identifier identifier, boolean redMatter) {
        super(identifier, Material.STONE);
        this.setHardness(1000000F);
        this.setDefaultState(this.getDefaultState().with(Properties.LIT, false).with(Properties.HORIZONTAL_FACING, Direction.NORTH));
        this.redMatter = redMatter;
        this.setLuminance((blockState) -> blockState.get(Properties.LIT) ? 14 : 0);
    }

    @Override
    public float getHardness() {
        return redMatter ? 2000000F : 1000000F;
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.LIT, Properties.HORIZONTAL_FACING);
    }

    public void updateFurnaceBlockState(boolean isActive, World world, int x, int y, int z) {
        BlockState blockState = world.getBlockState(x, y, z);
        BlockEntity blockEntity = world.getBlockEntity(x, y, z);
        ignoreBlockRemoval = true;
        world.setBlockState(x, y, z, blockState.with(Properties.LIT, isActive));
        ignoreBlockRemoval = false;
        blockEntity.cancelRemoval();
        world.setBlockEntity(x, y, z, blockEntity);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return redMatter ? new RedMatterFurnaceBlockEntity() : new DarkMatterFurnaceBlockEntity();
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if(!world.isRemote && world.getBlockEntity(x, y, z) instanceof DarkMatterFurnaceBlockEntity blockEntity) {
            GuiHelper.openGUI(player, ProjectE.NAMESPACE.id("dark_matter_furnace"), blockEntity.getInput(), new DarkMatterFurnaceScreenHandler(player.inventory, blockEntity), (messagePacket) -> {
                messagePacket.ints = new int[]{messagePacket.ints != null ? messagePacket.ints[0] : 0, x, y, z};
            });
            return true;
        }
        if(!world.isRemote && world.getBlockEntity(x, y, z) instanceof RedMatterFurnaceBlockEntity blockEntity) {
            GuiHelper.openGUI(player, ProjectE.NAMESPACE.id("red_matter_furnace"), blockEntity.getInput(), new RedMatterFurnaceScreenHandler(player.inventory, blockEntity), (messagePacket) -> {
                messagePacket.ints = new int[]{messagePacket.ints != null ? messagePacket.ints[0] : 0, x, y, z};
            });
        }
        return true;
    }

    @Override
    public void onBreak(World world, int x, int y, int z) {
        if(!ignoreBlockRemoval) {
            super.onBreak(world, x, y, z);
        }
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        BlockState blockState = world.getBlockState(x, y, z);
        if (blockState.contains(Properties.LIT) && blockState.contains(Properties.HORIZONTAL_FACING) && blockState.get(Properties.LIT)) {
            int direction = blockState.get(Properties.HORIZONTAL_FACING).getId();
            float var7 = (float)x + 0.5F;
            float var8 = (float)y + 0.0F + random.nextFloat() * 6.0F / 16.0F;
            float var9 = (float)z + 0.5F;
            float var10 = 0.52F;
            float var11 = random.nextFloat() * 0.6F - 0.3F;
            if (direction == 4) {
                world.addParticle("smoke", var7 - var10, var8, var9 + var11, 0.0F, 0.0F, 0.0F);
                world.addParticle("flame", var7 - var10, var8, var9 + var11, 0.0F, 0.0F, 0.0F);
            } else if (direction == 5) {
                world.addParticle("smoke", var7 + var10, var8, var9 + var11, 0.0F, 0.0F, 0.0F);
                world.addParticle("flame", var7 + var10, var8, var9 + var11, 0.0F, 0.0F, 0.0F);
            } else if (direction == 2) {
                world.addParticle("smoke", var7 + var11, var8, var9 - var10, 0.0F, 0.0F, 0.0F);
                world.addParticle("flame", var7 + var11, var8, var9 - var10, 0.0F, 0.0F, 0.0F);
            } else if (direction == 3) {
                world.addParticle("smoke", var7 + var11, var8, var9 + var10, 0.0F, 0.0F, 0.0F);
                world.addParticle("flame", var7 + var11, var8, var9 + var10, 0.0F, 0.0F, 0.0F);
            }

        }
    }
}
