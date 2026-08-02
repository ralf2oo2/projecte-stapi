package net.ralf2oo2.projecte.util;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.state.property.Property;
import net.modificationstation.stationapi.api.vanillafix.util.DyeColor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class WorldTransmutations {
    private static final List<Entry> ENTRIES = new ArrayList<>();

    static
    {
        registerDefault(Block.STONE, Block.COBBLESTONE, Block.GRASS_BLOCK);
        registerDefault(Block.COBBLESTONE, Block.STONE, Block.GRASS_BLOCK);
        registerDefault(Block.GRASS_BLOCK, Block.SAND, Block.COBBLESTONE);
        registerDefault(Block.DIRT, Block.SAND, Block.COBBLESTONE);
        registerDefault(Block.SAND, Block.GRASS_BLOCK, Block.COBBLESTONE);
        registerDefault(Block.GRAVEL, Block.SANDSTONE, null);
        registerDefault(Block.SANDSTONE, Block.GRAVEL, null);
        registerDefault(Block.WATER, Block.ICE, null);
        registerDefault(Block.ICE, Block.WATER, null);
        registerDefault(Block.LAVA, Block.OBSIDIAN, null);
        registerDefault(Block.OBSIDIAN, Block.LAVA, null);

        BlockState logState = Block.LOG.getDefaultState();
        for (int type = 0; type < 3; type++) {
            int nextType = (type + 1) % 3;
            int prevType = (type + 2) % 3;

            register(new TargetState(logState, type), new TargetState(logState, nextType), new TargetState(logState, prevType));
        }

        BlockState leafState = Block.LEAVES.getDefaultState();

        for (int type = 0; type < 3; type++) {
            int nextType = (type + 1) % 3;
            int prevType = (type + 2) % 3;

            for (int decayFlags = 0; decayFlags <= 12; decayFlags += 4) {
                int currentMeta = type | decayFlags;
                int nextMeta = nextType | decayFlags;
                int prevMeta = prevType | decayFlags;

                register(new TargetState(leafState, currentMeta), new TargetState(leafState, nextMeta), new TargetState(leafState, prevMeta));
            }
        }

        DyeColor[] colors = DyeColor.values();
        for (DyeColor dye : DyeColor.values())
        {
            BlockState state = Block.WOOL.getDefaultState();

            int currentMeta = dye.getId(); // or dye.getMeta() / dye.ordinal() depending on the enum API
            int nextMeta = colors[(dye.ordinal() + 1) % colors.length].getId();
            int prevMeta = colors[(dye.ordinal() + colors.length - 1) % colors.length].getId();

            register(new TargetState(state, currentMeta), new TargetState(state, nextMeta), new TargetState(state, prevMeta));
        }
    }

    private static BlockState cyclePropertyBackwards(BlockState state, Property<?> property)
    {
        BlockState result = state;
        for (int i = 0; i < property.getValues().size() - 1; i++)
        {
            result = result.cycle(property);
        }
        return result;
    }

    public static TargetState getWorldTransmutation(World world, BlockPos pos, boolean isSneaking)
    {
        return getWorldTransmutation(new TargetState(world.getBlockState(pos), world.getBlockMeta(pos.x, pos.y, pos.z)), isSneaking);
    }

    public static TargetState getWorldTransmutation(BlockState current, boolean isSneaking) {
        return getWorldTransmutation(new TargetState(current, 0), isSneaking);
    }

    public static TargetState getWorldTransmutation(TargetState current, boolean isSneaking)
    {
        for (Entry e : ENTRIES)
        {
            if (e.input.state == current.state && e.input.meta == current.meta)
            {
                Pair<TargetState, TargetState> result = e.outputs;
                return isSneaking ? (result.getRight() == null ? result.getLeft() : result.getRight()) : result.getLeft();
            }
        }

        return null;
    }

    public static List<Entry> getWorldTransmutations()
    {
        return ENTRIES;
    }

    public static void register(TargetState from, TargetState result, TargetState altResult)
    {
        ENTRIES.add(new Entry(from, ImmutablePair.of(result, altResult)));
    }

    public static void register(BlockState from, BlockState result, BlockState altResult)
    {
        ENTRIES.add(new Entry(new TargetState(from, 0), ImmutablePair.of(new TargetState(result, 0), new TargetState(altResult, 0))));
    }

    private static void registerDefault(Block from, Block result, Block altResult)
    {
        register(from.getDefaultState(), result.getDefaultState(), altResult == null ? null : altResult.getDefaultState());
    }

    public record TargetState(BlockState state, int meta) {}

    public static class Entry {
        public final TargetState input;
        public final Pair<TargetState, TargetState> outputs;

        public Entry(TargetState from, Pair<TargetState, TargetState> results)
        {
            this.input = from;
            this.outputs = results;
        }
    }
}
