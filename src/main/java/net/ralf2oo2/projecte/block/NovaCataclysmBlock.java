package net.ralf2oo2.projecte.block;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.entity.PrimedNovaCatalystEntity;
import net.ralf2oo2.projecte.listener.BlockListener;

public class NovaCataclysmBlock extends NovaCatalystBlock {
    public NovaCataclysmBlock(Identifier identifier) {
        super(identifier);
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
                this.dropStack(world, x, y, z, new ItemStack(BlockListener.novaCataclysm.asItem().id, 1, 0));
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
}
