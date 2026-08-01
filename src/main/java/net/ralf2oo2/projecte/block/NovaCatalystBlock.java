package net.ralf2oo2.projecte.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.entity.PrimedNovaCatalystEntity;
import net.ralf2oo2.projecte.listener.BlockListener;

import java.util.Random;

public class NovaCatalystBlock extends TemplateBlock {
    public NovaCatalystBlock(Identifier identifier) {
        super(identifier, Material.TNT);
    }

    @Override
    public void onPlaced(World world, int x, int y, int z) {
        super.onPlaced(world, x, y, z);
        if (world.isPowered(x, y, z)) {
            this.onMetadataChange(world, x, y, z, 1);
            world.setBlock(x, y, z, 0);
        }

    }

    @Override
    public void neighborUpdate(World world, int x, int y, int z, int id) {
        if (id > 0 && Block.BLOCKS[id].canEmitRedstonePower() && world.isPowered(x, y, z)) {
            this.onMetadataChange(world, x, y, z, 1);
            world.setBlock(x, y, z, 0);
        }

    }

    @Override
    public int getDroppedItemCount(Random random) {
        return 0;
    }

    @Override
    public void onDestroyedByExplosion(World world, int x, int y, int z) {
        if(!world.isRemote) {
            TntEntity var5 = new PrimedNovaCatalystEntity(world, (double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F));
            var5.fuse = world.random.nextInt(var5.fuse / 4) + var5.fuse / 8;
            world.spawnEntity(var5);
        }
    }

    @Override
    public void onMetadataChange(World world, int x, int y, int z, int meta) {
        if (!world.isRemote) {
            if ((meta & 1) == 0) {
                this.dropStack(world, x, y, z, new ItemStack(BlockListener.novaCatalyst.asItem().id, 1, 0));
            } else {
                this.explode(world, x, y, z, null);
            }

        }
    }

    public void explode(World world, int x, int y, int z, LivingEntity livingEntity) {
        if(!world.isRemote) {
            TntEntity var6 = new PrimedNovaCatalystEntity(world, ((float)x + 0.5F), ((float)y + 0.5F), ((float)z + 0.5F), livingEntity);
            world.spawnEntity(var6);
            world.playSound(var6, "random.fuse", 1.0F, 1.0F);
        }
    }

    @Override
    public void onBlockBreakStart(World world, int x, int y, int z, PlayerEntity player) {
        if (player.getHand() != null && player.getHand().itemId == Item.FLINT_AND_STEEL.id) {
            this.explode(world, x, y, z, player);
            world.setBlock(x, y, z, 0);
        }

        super.onBlockBreakStart(world, x, y, z, player);
    }
}
