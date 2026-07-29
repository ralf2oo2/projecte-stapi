package net.ralf2oo2.projecte.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.util.Namespace;
import net.ralf2oo2.projecte.api.registry.ConversionRegistry;
import net.ralf2oo2.projecte.emc.IngredientMap;
import net.ralf2oo2.projecte.emc.json.*;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ConversionRegistryImpl implements ConversionRegistry {
    public static final ConversionRegistryImpl INSTANCE = new ConversionRegistryImpl();

    final Map<Object, NormalizedSimpleStack> fakes = new HashMap<>();

    @Override
    public void addConversion(@NotNull Namespace namespace, int amount, @NotNull Object output, @NotNull Map<Object, Integer> ingredients) {
        NormalizedSimpleStack nssOut = objectToNSS(namespace, output);
        IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
        for (Map.Entry<Object, Integer> entry : ingredients.entrySet()) {
            NormalizedSimpleStack nss = objectToNSS(namespace, entry.getKey());
            ingredientMap.addIngredient(nss, entry.getValue());
        }
        List<APIConversion> conversionsFromMod;
        String modId = namespace.toString();
        if (storedConversions.containsKey(modId)) {
            conversionsFromMod = storedConversions.get(modId);
        } else {
            conversionsFromMod = Lists.newLinkedList();
            storedConversions.put(modId, conversionsFromMod);
        }
        conversionsFromMod.add(new APIConversion(amount, nssOut, ImmutableMap.copyOf(ingredientMap.getMap())));
    }

    public final Map<String, List<APIConversion>> storedConversions = new HashMap<>();

    public NormalizedSimpleStack objectToNSS(@NotNull Namespace namespace, Object object) {
        if (object instanceof Block) {
            return objectToNSS(namespace, new ItemStack((Block) object));
        } else if (object instanceof Item) {
            return objectToNSS(namespace, new ItemStack((Item) object));
        }

        if (object instanceof ItemStack) {
            return NSSItem.create((ItemStack) object);
        } else if (object instanceof FluidStack) {
            return NSSFluid.create(((FluidStack) object).fluid);
        } else if (object instanceof String) {
            return NSSTag.create((String) object);
        } else if (object != null && object.getClass().equals(Object.class)) {
            if (fakes.containsKey(object)) return fakes.get(object);

            NormalizedSimpleStack nss = NSSFake.create(fakes.size() + " by " + namespace);
            fakes.put(object, nss);
            return nss;
        } else {
            throw new IllegalArgumentException("Can not turn " + object + " (" + ClassUtils.getPackageCanonicalName(object, "") + ") into NormalizedSimpleStack. need ItemStack, FluidStack, String or 'Object'");
        }
    }

    public static class APIConversion {
        public final int amount;
        public final NormalizedSimpleStack output;
        public final ImmutableMap<NormalizedSimpleStack, Integer> ingredients;

        private APIConversion(int amount, NormalizedSimpleStack output, ImmutableMap<NormalizedSimpleStack, Integer> ingredients) {
            this.amount = amount;
            this.output = output;
            this.ingredients = ingredients;
        }
    }
}
