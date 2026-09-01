package net.ralf2oo2.projecte.item;

import net.danygames2014.nyalib.item.EnhancedPlacementContextItem;
import net.danygames2014.nyalib.particle.ParticleHelper;
import net.danygames2014.nyalib.sound.SoundHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.StationBlockPos;
import net.ralf2oo2.projecte.api.item.ExtraFunction;
import net.ralf2oo2.projecte.api.item.ProjectileShooter;
import net.ralf2oo2.projecte.entity.MobRandomizerProjectileEntity;
import net.ralf2oo2.projecte.util.BoxHelper;
import net.ralf2oo2.projecte.util.Sounds;
import net.ralf2oo2.projecte.util.WorldTransmutations;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static net.ralf2oo2.projecte.listener.KeyBindingListener.charge;

public class PhilosophersStoneItem extends ModeItem implements ProjectileShooter, ExtraFunction, EnhancedPlacementContextItem {
    public PhilosophersStoneItem(Identifier identifier) {
        super(identifier, (byte)4, new String[] {
                "pe.philstone.mode1",
                "pe.philstone.mode2",
                "pe.philstone.mode3"
        });
        this.setCraftingReturnItem(this);
        this.setMaxDamage(0);
    }

    @Override
    public boolean useOnBlock(ItemStack stack, PlayerEntity player, World world, int x, int y, int z, int side, Vec3d hitVec) {
        if(world.isRemote) {
            return true;
        }

        BlockPos pos = new BlockPos(x, y, z);

        WorldTransmutations.TargetState result = WorldTransmutations.getWorldTransmutation(world, pos, player.isSneaking());

        if(result != null) {
            int mode = this.getMode(stack);
            int charge = this.getCharge(stack);

            for (BlockPos currentPos : getAffectedPositions(world, pos, player, Direction.byId(side), mode, charge))
            {
                world.setBlockState(currentPos.x, currentPos.y, currentPos.z, result.state(), result.meta());
//                PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), currentPos, result, hand);
                if (world.random.nextInt(8) == 0)
                {
                    ParticleHelper.addParticle(world, "smoke", currentPos.getX(), currentPos.getY() + 1, currentPos.getZ(), 0, 0, 0);
                }
            }

            SoundHelper.playSound(player, Sounds.TRANSMUTE, 1, 1);

            return true;
        }
        return false;
    }

    @Override
    public boolean shootProjectile(@NotNull PlayerEntity player, @NotNull ItemStack stack) {
        World world = player.world;
        SoundHelper.playSound(player.world, player.x, player.y, player.z, Sounds.TRANSMUTE, 1, 1);
        MobRandomizerProjectileEntity ent = new MobRandomizerProjectileEntity(world, player);
        ent.setVelocity(player, player.pitch, player.yaw, 0, 1.5F, 1);
        world.spawnEntity(ent);
        return true;
    }

    //TODO: implement
    @Override
    public boolean doExtraFunction(@NotNull ItemStack stack, @NotNull PlayerEntity player) {
        return false;
    }

    public static Set<BlockPos> getAffectedPositions(World world, BlockPos pos, PlayerEntity player, Direction sideHit, int mode, int charge)
    {
        Set<BlockPos> ret = new HashSet<>();
        BlockState targeted = world.getBlockState(pos);
        int targetedMeta = world.getBlockMeta(pos.x, pos.y, pos.z);

        BlockPos min = pos;
        BlockPos max = pos;

        switch (mode)
        {
            case 0: // Cube
                min = pos.add(-charge, -charge, -charge);
                max = pos.add(charge, charge, charge);
                break;

            case 1: // Panel
                if (sideHit == Direction.UP || sideHit == Direction.DOWN) {
                    min = pos.add(-charge, 0, -charge);
                    max = pos.add(charge, 0, charge);
                } else if (sideHit == Direction.EAST || sideHit == Direction.WEST) {
                    min = pos.add(0, -charge, -charge);
                    max = pos.add(0, charge, charge);
                } else if (sideHit == Direction.SOUTH || sideHit == Direction.NORTH) {
                    min = pos.add(-charge, -charge, 0);
                    max = pos.add(charge, charge, 0);
                }
                break;

            case 2: // Line
                Direction playerFacing = Direction.fromRotation(player.yaw);
                if (playerFacing.getAxis() == Direction.Axis.Z) {
                    min = pos.add(0, 0, -charge);
                    max = pos.add(0, 0, charge);
                } else if (playerFacing.getAxis() == Direction.Axis.X) {
                    min = pos.add(-charge, 0, 0);
                    max = pos.add(charge, 0, 0);
                }
                break;
        }

        // Standard triple-nested loop is faster and safer against mutable stream cursors
        int minX = Math.min(min.x, max.x);
        int minY = Math.min(min.y, max.y);
        int minZ = Math.min(min.z, max.z);
        int maxX = Math.max(min.x, max.x);
        int maxY = Math.max(min.y, max.y);
        int maxZ = Math.max(min.z, max.z);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockState currentState = world.getBlockState(x, y, z);
                    int currentMeta = world.getBlockMeta(x, y, z);

                    // Use equals() for BlockState comparison
                    if (currentState.equals(targeted) && currentMeta == targetedMeta) {
                        ret.add(new BlockPos(x, y, z)); // Store distinct immutable BlockPos instances
                    }
                }
            }
        }

        return ret;
    }
}
