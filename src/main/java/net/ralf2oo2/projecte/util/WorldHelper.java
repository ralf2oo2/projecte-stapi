package net.ralf2oo2.projecte.util;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.StationBlockPos;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.config.Config;

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

    /**
     * Wrapper around BlockPos.getAllInBox() with an AABB
     * Note that this is inclusive of all positions in the AABB!
     */
    public static Iterable<BlockPos> getPositionsFromBox(Box box)
    {
        return StationBlockPos.stream(box).toList();
    }

    public static LivingEntity getRandomEntity(World world, LivingEntity toRandomize)
    {
        Class<? extends LivingEntity> entClass = toRandomize.getClass();

        if (peacefuls.contains(entClass))
        {
            return getNewEntityInstance(CollectionHelper.getRandomListEntry(peacefuls, entClass), world);
        }
        else if (mobs.contains(entClass))
        {
            LivingEntity ent = getNewEntityInstance(CollectionHelper.getRandomListEntry(mobs, entClass), world);
            return ent;
        }
        else if (world.random.nextInt(2) == 0)
        {
            return getNewEntityInstance(SlimeEntity.class, world);
        }
        else
        {
            return getNewEntityInstance(SheepEntity.class, world);
        }
    }
    public static List<BlockEntity> getTileEntitiesWithinAABB(World world, Box bBox)
    {
        List<BlockEntity> list = new ArrayList<>();

        for (BlockPos pos : getPositionsFromBox(bBox))
        {
            BlockEntity tile = world.getBlockEntity(pos.x, pos.y, pos.z);
            if (tile != null)
            {
                list.add(tile);
            }
        }

        return list;
    }

    /**
     * Gravitates an entity, vanilla xp orb style, towards a position
     * Code adapted from EntityXPOrb and OpenBlocks Vacuum Hopper, mostly the former
     */
    public static void gravitateEntityTowards(Entity ent, double x, double y, double z)
    {
        double dX = x - ent.x;
        double dY = y - ent.y;
        double dZ = z - ent.z;
        double dist = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

        double vel = 1.0 - dist / 15.0;
        if (vel > 0.0D)
        {
            vel *= vel;
            ent.velocityX += dX / dist * vel * 0.1;
            ent.velocityY += dY / dist * vel * 0.2;
            ent.velocityZ += dZ / dist * vel * 0.1;
        }
    }

//    public static void growNearbyRandomly(boolean harvest, World world, BlockPos pos, EntityPlayer player)
//    {
//        int chance = harvest ? 16 : 32;
//
//        for (BlockPos currentPos : BlockPos.getAllInBox(pos.add(-5, -3, -5), pos.add(5, 3, 5)))
//        {
//            IBlockState state = world.getBlockState(currentPos);
//            Block crop = state.getBlock();
//
//            // Vines, leaves, tallgrass, deadbush, doubleplants
//            if (crop instanceof IShearable)
//            {
//                if (harvest)
//                {
//                    world.destroyBlock(currentPos, true);
//                }
//            }
//            // Carrot, cocoa, wheat, grass (creates flowers and tall grass in vicinity),
//            // Mushroom, potato, sapling, stems, tallgrass
//            else if (crop instanceof IGrowable)
//            {
//                IGrowable growable = ((IGrowable) crop);
//                if (!growable.canGrow(world, currentPos, state, false))
//                {
//                    if (harvest
//                                && crop != Blocks.MELON_STEM && crop != Blocks.PUMPKIN_STEM
//                                && (player == null || PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), currentPos)))
//                    {
//                        world.destroyBlock(currentPos, true);
//                    }
//                }
//                else if (world.rand.nextInt(chance) == 0)
//                {
//                    if (ProjectEConfig.items.harvBandGrass || !crop.getTranslationKey().toLowerCase(Locale.ROOT).contains("grass"))
//                    {
//                        growable.grow(world, world.rand, currentPos, state);
//                    }
//                }
//            }
//            // All modded
//            // Cactus, Reeds, Netherwart, Flower
//            else if (crop instanceof IPlantable)
//            {
//                if (world.rand.nextInt(chance / 4) == 0)
//                {
//                    for (int i = 0; i < (harvest ? 8 : 4); i++)
//                    {
//                        crop.updateTick(world, currentPos, state, world.rand);
//                    }
//                }
//
//                if (harvest)
//                {
//                    if (crop instanceof BlockFlower)
//                    {
//                        if (player == null || PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), currentPos))
//                        {
//                            world.destroyBlock(currentPos, true);
//                        }
//                    }
//                    if (crop == Blocks.REEDS || crop == Blocks.CACTUS)
//                    {
//                        boolean shouldHarvest = true;
//
//                        for (int i = 1; i < 3; i++)
//                        {
//                            if (world.getBlockState(currentPos.up(i)).getBlock() != crop)
//                            {
//                                shouldHarvest = false;
//                                break;
//                            }
//                        }
//
//                        if (shouldHarvest)
//                        {
//                            for (int i = crop == Blocks.REEDS ? 1 : 0; i < 3; i++)
//                            {
//                                if (player != null && PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), currentPos.up(i)))
//                                {
//                                    world.destroyBlock(currentPos.up(i), true);
//                                } else if (player == null)
//                                {
//                                    world.destroyBlock(currentPos.up(i), true);
//                                }
//                            }
//                        }
//                    }
//                    if (crop == Blocks.NETHER_WART)
//                    {
//                        int age = state.getValue(BlockNetherWart.AGE);
//                        if (age == 3)
//                        {
//                            if (player == null || player != null && PlayerHelper.hasBreakPermission(((EntityPlayerMP) player), currentPos))
//                            {
//                                world.destroyBlock(currentPos, true);
//                            }
//                        }
//                    }
//                }
//
//            }
//        }
//    }

    /**
     * Recursively mines out a vein of the given Block, starting from the provided coordinates
     */
    public static int harvestVein(World world, PlayerEntity player, ItemStack stack, BlockPos pos, BlockState target, List<ItemStack> currentDrops, int numMined)
    {
        if (numMined >= Constants.MAX_VEIN_SIZE)
        {
            return numMined;
        }

        Box b = Box.create(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);

        for (BlockPos currentPos : getPositionsFromBox(b))
        {
            BlockState currentState = world.getBlockState(currentPos);
            Block block = currentState.getBlock();

            if (currentState == target || (target == Block.LIT_REDSTONE_ORE.getDefaultState() && block == Block.REDSTONE_ORE))
            {
                numMined++;
                currentDrops.addAll(getBlockDrops(world, player, currentState, stack, currentPos));
                world.setBlock(currentPos.x, currentPos.y, currentPos.z, 0);
                numMined = harvestVein(world, player, stack, currentPos, target, currentDrops, numMined);
                if (numMined >= Constants.MAX_VEIN_SIZE) {
                    break;
                }
            }
        }
        return numMined;
    }

    public static void igniteNearby(World world, PlayerEntity player)
    {
        for (BlockPos pos : StationBlockPos.stream(new BlockPos((int) player.x, (int) player.y, (int) player.z).add(-8, -5, -8), new BlockPos((int) player.x, (int) player.y, (int) player.z).add(8, 5, 8)).toList())
        {
            if (world.random.nextInt(128) == 0 && world.isAir(pos.x, pos.y, pos.z))
            {
                // TODO: check if this works
                world.setBlockState(pos.toImmutable(), Block.FIRE.getDefaultState());
            }
        }
    }

    /**
     * Repels projectiles and mobs in the given AABB away from a given point
     */
    public static void repelEntitiesInAABBFromPoint(World world, Box effectBounds, double x, double y, double z, boolean isSWRG)
    {
        List<Entity> list = world.collectEntitiesByClass(Entity.class, effectBounds);

        for (Entity ent : list)
        {
            if ((isSWRG && !swrgBlacklist.contains(ent.getClass()))
                        || (!isSWRG && !interdictionBlacklist.contains(ent.getClass()))) {
                if ((ent instanceof LivingEntity) || (ent instanceof ArrowEntity))
                {
                    if (!isSWRG && Config.EFFECT_CONFIG.interdictionMode && !(ent instanceof MobEntity || ent instanceof ArrowEntity))
                    {
                        continue;
                    }
                    else
                    {
                        if (ent instanceof ArrowEntity arrowEntity && arrowEntity.onGround)
                        {
                            continue;
                        }
                        Vec3d p = Vec3d.create(x, y, z);
                        Vec3d t = Vec3d.create(ent.x, ent.y, ent.z);
                        double distance = p.distanceTo(t) + 0.1D;

                        Vec3d r = Vec3d.create(t.x - p.x, t.y - p.y, t.z - p.z);

                        ent.velocityX += r.x / 1.5D / distance;
                        ent.velocityY += r.y / 1.5D / distance;
                        ent.velocityZ += r.z / 1.5D / distance;
                    }
                }
            }
        }
    }

}
