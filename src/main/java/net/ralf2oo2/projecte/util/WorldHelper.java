package net.ralf2oo2.projecte.util;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.StationBlockPos;
import net.ralf2oo2.projecte.ProjectE;

import java.util.*;

public class WorldHelper {
    private static final List<Class<? extends LivingEntity>> peacefuls = Lists.newArrayList(
            SheepEntity.class, PigEntity.class, CowEntity.class,
            ChickenEntity.class, SquidEntity.class, WolfEntity.class
    );

    private static final List<Class<? extends LivingEntity>> mobs = Lists.newArrayList(
            ZombieEntity.class, SkeletonEntity.class, CreeperEntity.class,
            SpiderEntity.class, GhastEntity.class, SlimeEntity.class
    );

    private static final Set<Class<? extends Entity>> interdictionBlacklist = new HashSet<>();

    private static final Set<Class<? extends Entity>> swrgBlacklist = new HashSet<>();

    public static boolean blacklistInterdiction(Class<? extends Entity> clazz)
    {
        if (!interdictionBlacklist.contains(clazz))
        {
            interdictionBlacklist.add(clazz);
            return true;
        }
        return false;
    }

    public static boolean blacklistSwrg(Class<? extends Entity> clazz)
    {
        if (!interdictionBlacklist.contains(clazz))
        {
            interdictionBlacklist.add(clazz);
            return true;
        }
        return false;
    }

    public static boolean addPeaceful(Class<? extends LivingEntity> clazz)
    {
        if (!peacefuls.contains(clazz))
        {
            peacefuls.add(clazz);
            return true;
        }
        return false;
    }

    public static boolean removePeaceful(Class<? extends LivingEntity> clazz)
    {
        return peacefuls.remove(clazz);
    }

    public static void clearPeacefuls()
    {
        peacefuls.clear();
    }

    public static boolean addMob(Class<? extends LivingEntity> clazz)
    {
        if (!mobs.contains(clazz))
        {
            mobs.add(clazz);
            return true;
        }
        return false;
    }

    public static boolean removeMob(Class<? extends LivingEntity> clazz)
    {
        return mobs.remove(clazz);
    }

    public static void clearMobs()
    {
        mobs.clear();
    }

    public static void createLootDrop(List<ItemStack> drops, World world, BlockPos pos)
    {
        createLootDrop(drops, world, pos.getX(), pos.getY(), pos.getZ());
    }

    public static void createLootDrop(List<ItemStack> drops, World world, double x, double y, double z)
    {
        ItemHelper.compactItemListNoStacksize(drops);

        for (ItemStack drop : drops)
        {
            ItemEntity ent = new ItemEntity(world, x, y, z, drop);
            world.spawnEntity(ent);
        }
    }

    /**
     * Equivalent of World.newExplosion
     */
    public static void createNovaExplosion(World world, Entity exploder, double x, double y, double z, float power)
    {
        NovaExplosion explosion = new NovaExplosion(world, exploder, x, y, z, power);
        explosion.explode();
        explosion.playExplosionSound(true);
    }

    public static void dropInventory(Inventory inv, World world, BlockPos pos)
    {
        if (inv == null)
            return;

        for (int i = 0; i < inv.size(); i++)
        {
            ItemStack stack = inv.getStack(i);

            if (!StackUtil.isEmpty(stack))
            {
                ItemEntity ent = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                world.spawnEntity(ent);
            }
        }
    }

    public static void extinguishNearby(World world, PlayerEntity player)
    {
        for (BlockPos pos : StationBlockPos.stream(new BlockPos((int) player.x, (int) player.y, (int) player.z).add(-1, -1, -1), new BlockPos((int) player.x, (int) player.y, (int) player.z).add(1, 1, 1)).toList())
        {
            if (world.getBlockState(pos).getBlock() == Block.FIRE) // PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), pos)
            {
                world.setBlock(pos.x, pos.y, pos.z, 0);
            }
        }
    }

//    public static void freezeInBoundingBox(World world, Box box, PlayerEntity player, boolean random)
//    {
//        for (BlockPos pos : getPositionsFromBox(box))
//        {
//            Block b = world.getBlockState(pos).getBlock();
//
//            if ((b == Block.WATER || b == Block.FLOWING_WATER) && (!random || world.random.nextInt(128) == 0))
//            {
//                if (player != null)
//                {
//                    PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), pos, Block.ICE.getDefaultState());
//                }
//                else
//                {
//                    world.setBlockState(pos, Block.ICE.getDefaultState());
//                }
//            }
//            else if (b.isSolidFace(world, pos.x, pos.y, pos.z, Direction.UP.getId()))
//            {
//                BlockPos up = pos.up();
//                BlockState stateUp = world.getBlockState(up);
//                BlockState newState = null;
//
//                if (stateUp.getBlock().isAir(stateUp, world, up) && (!random || world.random.nextInt(128) == 0))
//                {
//                    newState = Block.SNOW.getDefaultState();
//                } else if (stateUp.getBlock() == Block.SNOW && stateUp.getValue(BlockSnow.LAYERS) < 8
//                                   && world.rand.nextInt(512) == 0)
//                {
//                    newState = stateUp.withProperty(BlockSnow.LAYERS, stateUp.getValue(BlockSnow.LAYERS) + 1);
//                }
//
//                if (newState != null)
//                {
//                    if (player != null)
//                    {
//                        PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), up, newState);
//                    }
//                    else
//                    {
//                        world.setBlockState(up, newState);
//                    }
//                }
//            }
//        }
//    }

    public static Map<Direction, BlockEntity> getAdjacentBlockEntitiesMapped(final World world, final BlockEntity blockEntity)
    {
        Map<Direction, BlockEntity> ret = new EnumMap<>(Direction.class);

        for (Direction dir : Direction.values()) {
            BlockPos blockPos = new BlockPos(blockEntity.x, blockEntity.y, blockEntity.z).offset(dir);
            BlockEntity candidate = world.getBlockEntity(blockPos.x, blockPos.y, blockPos.z);
            if (candidate != null) {
                ret.put(dir, candidate);
            }
        }

        return ret;
    }

    public static List<ItemStack> getBlockDrops(World world, PlayerEntity player, BlockState state, ItemStack stack, BlockPos pos)
    {
//        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0 && state.getBlock().canSilkHarvest(world, pos, state, player))
//        {
//            return Lists.newArrayList(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));
//        }

        return state.getBlock().getDropList(world, pos.x, pos.y, pos.z, state, world.getBlockMeta(pos.x, pos.y, pos.z));
    }

    /**
     * Gets an AABB for AOE digging operations. The offset increases both the breadth and depth of the box.
     */
    public static Box getBroadDeepBox(BlockPos pos, Direction direction, int offset)
    {
        switch (direction)
        {
            case EAST: return Box.create(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX(), pos.getY() + offset, pos.getZ() + offset);
            case WEST: return Box.create(pos.getX(), pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
            case UP: return Box.create(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY(), pos.getZ() + offset);
            case DOWN: return Box.create(pos.getX() - offset, pos.getY(), pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
            case SOUTH: return Box.create(pos.getX() - offset, pos.getY() - offset, pos.getZ() - offset, pos.getX() + offset, pos.getY() + offset, pos.getZ());
            case NORTH: return Box.create(pos.getX() - offset, pos.getY() - offset, pos.getZ(), pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
            default: return Box.create(0, 0, 0, 0, 0, 0);
        }
    }

    /**
     * Returns in AABB that is always 3x3 orthogonal to the side hit, but varies in depth in the direction of the side hit
     */
    public static Box getDeepBox(BlockPos pos, Direction direction, int depth)
    {
        switch (direction)
        {
            case EAST: return Box.create(pos.getX() - depth, pos.getY() - 1, pos.getZ() - 1, pos.getX(), pos.getY() + 1, pos.getZ() + 1);
            case WEST: return Box.create(pos.getX(), pos.getY() - 1, pos.getZ() - 1, pos.getX() + depth, pos.getY() + 1, pos.getZ() + 1);
            case UP: return Box.create(pos.getX() - 1, pos.getY() - depth, pos.getZ() - 1, pos.getX() + 1, pos.getY(), pos.getZ() + 1);
            case DOWN: return Box.create(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 1, pos.getY() + depth, pos.getZ() + 1);
            case SOUTH: return Box.create(pos.getX() - 1, pos.getY() - 1, pos.getZ() - depth, pos.getX() + 1, pos.getY() + 1, pos.getZ());
            case NORTH: return Box.create(pos.getX() - 1, pos.getY() - 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + depth);
            default: return Box.create(0, 0, 0, 0, 0, 0);
        }
    }

    /**
     * Gets an AABB for AOE digging operations. The charge increases only the breadth of the box.
     * Y level remains constant. As such, a direction hit is unneeded.
     */
    public static Box getFlatYBox(BlockPos pos, int offset)
    {
        return Box.create(pos.getX() - offset, pos.getY(), pos.getZ() - offset, pos.getX() + offset, pos.getY(), pos.getZ() + offset);
    }

    public static <T extends Entity> T getNewEntityInstance(Class<T> c, World world)
    {
        try
        {
            return c.getConstructor(World.class).newInstance(world);
        }
        catch (Exception e)
        {
            ProjectE.LOGGER.fatal("Could not create new entity instance for: {}", c.getCanonicalName());
            e.printStackTrace();
        }

        return null;
    }

    // TODO: port rest of class if needed
}
