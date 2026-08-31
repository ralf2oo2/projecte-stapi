package net.ralf2oo2.projecte.item;

import net.danygames2014.nyalib.particle.ParticleHelper;
import net.danygames2014.nyalib.sound.SoundHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.ralf2oo2.projecte.api.item.ItemCharge;
import net.ralf2oo2.projecte.api.item.ItemWithDisplayDurability;
import net.ralf2oo2.projecte.util.Sounds;
import net.ralf2oo2.projecte.util.WorldHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DestructionCatalystItem extends ProjectEItem implements ItemCharge, ItemWithDisplayDurability {
    public DestructionCatalystItem(Identifier identifier) {
        super(identifier);
        setMaxCount(1);
    }

    @Override
    public boolean useOnBlock(ItemStack stack, PlayerEntity user, World world, int x, int y, int z, int side) {
        if(world.isRemote) {
            return true;
        }

        BlockPos coords = new BlockPos(x, y, z);

        int numRows = calculateDepthFromCharge(stack);
        boolean hasAction = false;

        Box box = WorldHelper.getDeepBox(coords, Direction.byId(side), --numRows);

        List<ItemStack> drops = new ArrayList<>();

        for (BlockPos pos : WorldHelper.getPositionsFromBox(box))
        {
            BlockState state = world.getBlockState(pos);
            float hardness = state.getHardness(world, pos);

            if (world.isAir(pos.x, pos.y, pos.z) || hardness >= 50.0F || hardness == -1.0F)
            {
                continue;
            }

            if (!consumeFuel(user, stack, 8, true))
            {
                break;
            }

            hasAction = true;

            List<ItemStack> list = WorldHelper.getBlockDrops(world, user, state, stack, pos);
            if (!list.isEmpty()) {
                drops.addAll(list);
            }

            world.setBlock(pos.x, pos.y, pos.z, 0);

            if (world.random.nextInt(8) == 0)
            {
                ParticleHelper.addParticle(world, world.random.nextBoolean() ? "explode" : "smoke", pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
            }
        }

//        PlayerHelper.swingItem(player, hand);
        if (hasAction)
        {
            WorldHelper.createLootDrop(drops, world, coords);
            SoundHelper.playSound(user, Sounds.DESTRUCT, 1.0F, 1.0F);
        }

        return true;
    }

    private int calculateDepthFromCharge(ItemStack stack)
    {
        int charge = getCharge(stack);
        if (charge <= 0)
        {
            return 1;
        }
        if (this instanceof CatalyticLensItem)
        {
            return 8 + (charge * 8);

        }
        return (int) Math.pow(2, 1 + charge);
    }

    @Override
    public int getNumCharges(@NotNull ItemStack stack) {
        return 3;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0D - (double) getCharge(stack) / getNumCharges(stack);
    }
}
