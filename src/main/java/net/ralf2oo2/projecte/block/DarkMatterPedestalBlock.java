package net.ralf2oo2.projecte.block;

import net.danygames2014.nyalib.block.HasBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.api.item.PedestalItem;
import net.ralf2oo2.projecte.block.entity.DarkMatterFurnaceBlockEntity;
import net.ralf2oo2.projecte.block.entity.DarkMatterPedestalBlockEntity;
import net.ralf2oo2.projecte.util.StackUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DarkMatterPedestalBlock extends TemplateBlock implements HasBlockEntity, CustomTooltipProvider {
    private static final Box AABB = Box.create(0.1875, 0, 0.1875, 0.8125, 0.75, 0.8125);

    public DarkMatterPedestalBlock(Identifier identifier) {
        super(identifier, Material.STONE);
        this.setHardness(1.0F);
        this.setLuminance(1.0F);
    }

    @Override
    public Box getBoundingBox(World world, int x, int y, int z) {
        return AABB;
    }

    private void dropItem(World world, int x, int y, int z)
    {
        BlockEntity te = world.getBlockEntity(x, y, z);
        if (te instanceof DarkMatterPedestalBlockEntity dm)
        {
            ItemStack stack = dm.getInventory().getStack(0);
            if (!StackUtil.isEmpty(stack))
            {
                dm.getInventory().setStack(0, null);
                ItemEntity ent = new ItemEntity(world, x, y + 0.8, z, stack);
                world.spawnEntity(ent);
            }
        }
    }

    @Override
    public void onBreak(World world, int x, int y, int z) {
        dropItem(world, x, y, z);
        super.onBreak(world, x, y, z);
    }

    @Override
    public void onBlockBreakStart(World world, int x, int y, int z, PlayerEntity player) {
        if(!world.isRemote) {
            dropItem(world, x, y, z);
            BlockState state = world.getBlockState(x, y, z);
            world.notifyNeighbors(x, y, z, state.getBlock().id);
        }
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if (!world.isRemote)
        {
            BlockState state = world.getBlockState(x, y, z);
            BlockEntity te = world.getBlockEntity(x, y, z);
            if (!(te instanceof DarkMatterPedestalBlockEntity dm))
            {
                return true;
            }

            ItemStack item = dm.getInventory().getStack(0);
            ItemStack stack = player.getHand();

            if (StackUtil.isEmpty(stack)
                        && !StackUtil.isEmpty(item)
                        && item.getItem() instanceof PedestalItem)
            {
                dm.setActive(!dm.getActive());
                world.notifyNeighbors(x, y, z, state.getBlock().id);
            } else if (!StackUtil.isEmpty(stack) && StackUtil.isEmpty(item))
            {
                dm.getInventory().setStack(0, stack.split(1));
                if (stack.count <= 0)
                {
                    player.inventory.main[player.inventory.selectedSlot] = null;
                }
                world.notifyNeighbors(x, y, z, state.getBlock().id);
            }
        }
        return true;
    }

    @Override
    public void neighborUpdate(World world, int x, int y, int z, int id) {
        boolean flag = world.isPowered(x, y, z);
        BlockEntity te = world.getBlockEntity(x, y, z);

        if (te instanceof DarkMatterPedestalBlockEntity dm)
        {

            if (dm.previousRedstoneState != flag)
            {
                if (flag && !StackUtil.isEmpty(dm.getInventory().getStack(0)) && dm.getInventory().getStack(0).getItem() instanceof PedestalItem)
                {
                    BlockState state = world.getBlockState(x, y, z);
                    dm.setActive(!dm.getActive());
                    world.notifyNeighbors(x, y, z, state.getBlock().id);
                }

                dm.previousRedstoneState = flag;
            }
        }
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
    public BlockEntity createBlockEntity(World world, int i, int i1, int i2) {
        return new DarkMatterPedestalBlockEntity();
    }

    @Override
    public @NotNull String[] getTooltip(ItemStack stack, String originalTooltip) {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(originalTooltip);
        tooltip.add(I18n.getTranslation("pe.pedestal.tooltip1"));
        tooltip.add(I18n.getTranslation("pe.pedestal.tooltip2"));
        return tooltip.toArray(new String[0]);
    }
}
