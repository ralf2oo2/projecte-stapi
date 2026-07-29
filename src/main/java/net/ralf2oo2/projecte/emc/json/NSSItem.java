package net.ralf2oo2.projecte.emc.json;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.util.StackUtil;

import java.util.*;

public class NSSItem implements NormalizedSimpleStack {
    public static final Map<String, Set<Integer>> usedMetadataMap = new HashMap<>();

    public final String itemName;
    public final int damage;

    NSSItem(String itemName, int damage) {
        this.itemName = itemName;
        this.damage = damage;
    }

    public static NormalizedSimpleStack create(Block block) {
        return create(block, 0);
    }

    public static NormalizedSimpleStack create(Block block, int meta) {
        return create(BlockRegistry.INSTANCE.getId(block), meta);
    }

    public static NormalizedSimpleStack create(ItemStack stack) {
        if (StackUtil.isEmpty(stack)) return null;
        return create(stack.getItem(), stack.getDamage());
    }

    public static NormalizedSimpleStack create(Item item) {
        return create(item, 0);
    }

    private static NormalizedSimpleStack create(Item item, int meta) {

        return create(ItemRegistry.INSTANCE.getId(item), meta);
    }

    private static NormalizedSimpleStack create(Identifier uniqueIdentifier, int damage) {
        if (uniqueIdentifier == null) return null;
        return create(uniqueIdentifier.toString(), damage);
    }

    public static NormalizedSimpleStack create(String itemName, int damage) {
        NSSItem normStack;
        try {
            normStack = new NSSItem(itemName, damage);
        } catch (Exception e) {
            ProjectE.LOGGER.fatal("Could not create NSSItem: {}", e.getMessage());
            return null;
        }
        usedMetadataMap.computeIfAbsent(itemName, k -> new HashSet<>()).add(damage);
        return normStack;
    }

    public static Set<NormalizedSimpleStack> getVariants(Identifier itemId) {
        Set<Integer> metaSet = usedMetadataMap.get(itemId);

        if (metaSet == null || metaSet.isEmpty()) {
            return Collections.singleton(NSSItem.create(itemId, 0));
        }

        Set<NormalizedSimpleStack> variants = new HashSet<>();
        for (int meta : metaSet) {
            variants.add(NSSItem.create(itemId, meta));
        }
        return variants;
    }

    @Override
    public int hashCode() {
        return itemName.hashCode() ^ damage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NSSItem other) {
            return this.itemName.equals(other.itemName) && this.damage == other.damage;
        }

        return false;
    }

    @Override
    public String json() {
        return String.format("%s|%s", itemName, damage == ProjectE.WILDCARD_VALUE ? "*" : damage);
    }

    @Override
    public String toString() {
        return String.format("%s:%s", itemName, damage == ProjectE.WILDCARD_VALUE ? "*" : damage);
    }
}
