package net.ralf2oo2.projecte.emc.mapper;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.common.collect.ImmutableMap;
import net.danygames2014.nyalib.fluid.Fluid;
import net.danygames2014.nyalib.fluid.FluidRegistry;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.danygames2014.nyalib.fluid.Fluids;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.item.Items;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.emc.arithmetic.FullBigFractionArithmetic;
import net.ralf2oo2.projecte.emc.collector.ExtendedMappingCollector;
import net.ralf2oo2.projecte.emc.collector.MappingCollector;
import net.ralf2oo2.projecte.emc.json.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.fraction.BigFraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FluidMapper implements EMCMapper<NormalizedSimpleStack, Long> {
    private static final List<Pair<NormalizedSimpleStack, FluidStack>> melting = new ArrayList<>();

    private static void addMelting(String tag, Identifier fluidId, int amount) {
        addMelting(NSSTag.create(tag), fluidId, amount);
    }

    private static void addMelting(Item item, Identifier fluidId, int amount) {
        addMelting(NSSItem.create(item), fluidId, amount);
    }

    private static void addMelting(Block block, Identifier fluidId, int amount) {
        addMelting(NSSItem.create(block), fluidId, amount);
    }

    private static void addMelting(NormalizedSimpleStack stack, Identifier fluidId, int amount) {
        Fluid fluid = FluidRegistry.getRegistry().get(fluidId);
        if (fluid != null) {
            melting.add(Pair.of(stack, new FluidStack(fluid, amount)));
        } else {
            ProjectE.LOGGER.warn("Can not get Fluid '{}'", fluidId);
        }
    }
    static {
        // TODO: for when someone finally ports tinkers (probably me and dany)

//        addMelting(Blocks.OBSIDIAN, "obisidan.molten", 288);
//        addMelting(Blocks.GLASS, "glass.molten", 1000);
//        addMelting(Blocks.GLASS_PANE, "glass.molten", 250);
//        addMelting(Items.ENDER_PEARL, "ender", 250);
//
//        addMelting("ingotIron", "iron.molten", 144);
//        addMelting("ingotGold", "gold.molten", 144);
//        addMelting("ingotCopper", "copper.molten", 144);
//        addMelting("ingotTin", "tin.molten", 144);
//        addMelting("ingotSilver", "silver.molten", 144);
//        addMelting("ingotLead", "lead.molten", 144);
//        addMelting("ingotNickel", "nickel.molten", 144);
//        addMelting("ingotAluminum", "aluminum.molten", 144);
//        addMelting("ingotArdite", "ardite.molten", 144);
//        addMelting("ingotCobalt", "cobalt.molten", 144);
//        addMelting("ingotPlatinum", "platinum.molten", 144);
//        addMelting("ingotObsidian", "obsidian.molten", 144);
//        addMelting("ingotElectrum", "electrum.molten", 144);
//        addMelting("ingotInvar", "invar.molten", 144);
//        addMelting("ingotSignalum", "signalum.molten", 144);
//        addMelting("ingotLumium", "lumium.molten", 144);
//        addMelting("ingotEnderium", "enderium.molten", 144);
//        addMelting("ingotMithril", "mithril.molten", 144);
//
//        addMelting("ingotBronze", "bronze.molten", 144);
//        addMelting("ingotAluminumBrass", "aluminumbrass.molten", 144);
//        addMelting("ingotManyullyn", "manyullyn.molten", 144);
//        addMelting("ingotAlumite", "alumite.molten", 144);
//
//        addMelting("gemEmerald", "emerald.liquid", 640);
//        addMelting("dustRedstone", "redstone", 100);
//        addMelting("dustGlowstone", "glowstone", 250);
//
//        addMelting("dustCryotheum", "cryotheum", 100);
//        addMelting("dustPryotheum", "pryotheum", 100);
    }

    @Override
    public void addMappings(MappingCollector<NormalizedSimpleStack, Long> mapper, CommentedConfig config) {
        mapper.addConversion(1000, NSSFluid.create(Fluids.WATER), Collections.singletonList(NSSItem.create(Block.ICE)));

        // 2. 1000 mB Lava = 1 Obsidian Block
        mapper.addConversion(1000, NSSFluid.create(Fluids.LAVA), Collections.singletonList(NSSItem.create(Block.OBSIDIAN)));

        if (!(mapper instanceof ExtendedMappingCollector emapper)) throw new RuntimeException("Cannot add Extended Fluid Mappings to mapper!");
        FullBigFractionArithmetic fluidArithmetic = new FullBigFractionArithmetic();

        for (Pair<NormalizedSimpleStack, FluidStack> pair: melting) {
            emapper.addConversion(pair.getValue().amount, NSSFluid.create(pair.getValue().fluid), Collections.singletonList(pair.getKey()), fluidArithmetic);
        }

        for (Fluid fluid : FluidRegistry.getRegistry().values()) {
            Item bucketItem = fluid.getBucketItem();

            if (bucketItem != null) {
                mapper.addConversion(
                        1,
                        NSSItem.create(bucketItem),
                        ImmutableMap.of(
                                NSSItem.create(Item.BUCKET), 1,
                                NSSFluid.create(fluid), 1000
                        )
                );
            }
        }
    }

    @Override
    public String getName() {
        return "FluidMapper";
    }

    @Override
    public String getDescription() {
        return "Adds Conversions for fluid container items and fluids.";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
