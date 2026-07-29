package net.ralf2oo2.projecte.emc.json;

import com.google.gson.*;
import net.danygames2014.nyalib.fluid.Fluid;
import net.danygames2014.nyalib.fluid.FluidRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.tag.TagKey;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.emc.collector.MappingCollector;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface NormalizedSimpleStack {

    enum Serializer implements JsonSerializer<NormalizedSimpleStack>, JsonDeserializer<NormalizedSimpleStack> {
        INSTANCE;

        @Override
        public NormalizedSimpleStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String s = json.getAsString();
            if (s.startsWith("TAG|")) {
                return NSSTag.create(s.substring("TAG|".length()));
            } else if (s.startsWith("FAKE|")) {
                return NSSFake.create(s.substring("FAKE|".length()));
            } else if (s.startsWith("FLUID|")) {
                String fluidName = s.substring("FLUID|".length());
                Fluid fluid = FluidRegistry.get(Identifier.of(fluidName));
                if (fluid == null)
                    throw new JsonParseException("Tried to identify nonexistent Fluid " + fluidName);
                return NSSFluid.create(fluid);
            } else {
                int pipeIndex = s.lastIndexOf('|');
                if (pipeIndex < 0)
                {
                    throw new JsonParseException(String.format("Cannot parse '%s' as itemstack. Missing | to separate metadata.", s));
                }
                String itemName = s.substring(0, pipeIndex);
                String itemDamageString = s.substring(pipeIndex + 1);
                int itemDamage;
                if (itemDamageString.equals("*")) {
                    itemDamage = ProjectE.WILDCARD_VALUE;
                }
                else {
                    try {
                        itemDamage = Integer.parseInt(itemDamageString);
                    } catch (NumberFormatException e) {
                        throw new JsonParseException(String.format("Could not parse '%s' to metadata-integer", itemDamageString), e);
                    }
                }

                return NSSItem.create(itemName, itemDamage);
            }
        }

        @Override
        public JsonElement serialize(NormalizedSimpleStack src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.json());
        }
    }

    static <V extends Comparable<V>> void addMappings(MappingCollector<NormalizedSimpleStack, V> mapper) {
        // Add conversions for all variants -> wildcard variant
        for (Map.Entry<String, Set<Integer>> entry : NSSItem.usedMetadataMap.entrySet()) {
            NormalizedSimpleStack stackWildcard = NSSItem.create(entry.getKey(), ProjectE.WILDCARD_VALUE);
            for (int metadata : entry.getValue()) {
                if (metadata != ProjectE.WILDCARD_VALUE) {
                    mapper.addConversion(1, stackWildcard, Collections.singletonList(NSSItem.create(entry.getKey(), metadata)));
                }
            }
        }

        // Add conversions for all variants <-> NSSTag
        for (Map.Entry<TagKey<Item>, NormalizedSimpleStack> entry : NSSTag.tagStacks.entrySet()) {
            NormalizedSimpleStack tagNss = entry.getValue();
            TagKey<Item> tagKey = entry.getKey();

            var entryList = ItemRegistry.INSTANCE.getOrCreateEntryList(tagKey);
            for (var registryEntry : entryList) {
                Item item = registryEntry.value();
                NormalizedSimpleStack itemNss = NSSItem.create(item);

                if (itemNss != null) {
                    mapper.addConversion(1, tagNss, Collections.singletonList(itemNss));
                    mapper.addConversion(1, itemNss, Collections.singletonList(tagNss));
                }
            }
        }
    }

    @Override
    boolean equals(Object o);

    String json();
}