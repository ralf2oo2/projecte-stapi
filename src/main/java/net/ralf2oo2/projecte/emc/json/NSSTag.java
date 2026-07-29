package net.ralf2oo2.projecte.emc.json;

import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.registry.RegistryEntry;
import net.modificationstation.stationapi.api.tag.TagKey;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NSSTag implements NormalizedSimpleStack {
    public static final Map<TagKey<Item>, NormalizedSimpleStack> tagStacks = new HashMap<>();

    public final TagKey<Item> tagKey;

    private NSSTag(TagKey<Item> tagKey) {
        this.tagKey = tagKey;
    }

    @Nullable
    public static NormalizedSimpleStack create(TagKey<Item> tagKey) {
        if (tagKey == null) return null;
        return tagStacks.computeIfAbsent(tagKey, NSSTag::new);
    }

    @Nullable
    public static NormalizedSimpleStack create(String tagName) {
        if (tagName == null || tagName.isEmpty()) return null;

//        if (tagName.startsWith("TAG|")) {
//            tagName = tagName.substring(4);
//        }

        Identifier id = Identifier.of(tagName);

        TagKey<Item> tagKey = TagKey.of(ItemRegistry.KEY, id);
        return create(tagKey);
    }

    public List<NSSItem> getMembers() {
        var entryList = ItemRegistry.INSTANCE.getOrCreateEntryList(this.tagKey);
        if (entryList == null) return Collections.emptyList();

        List<NSSItem> members = new ArrayList<>();

        for (RegistryEntry<Item> entry : entryList) {
            Item item = entry.value();
            Identifier itemId = ItemRegistry.INSTANCE.getId(item);

            if (itemId != null) {
                Set<Integer> variants = NSSItem.usedMetadataMap.get(itemId);

                if (variants != null && !variants.isEmpty()) {
                    for (int meta : variants) {
                        members.add((NSSItem) NSSItem.create(itemId.toString(), meta));
                    }
                } else {
                    members.add((NSSItem) NSSItem.create(itemId.toString(), 0));
                }
            }
        }

        return members;
    }

    @Override
    public int hashCode() {
        return tagKey.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NSSTag other && this.tagKey.equals(other.tagKey);
    }

    @Override
    public String json() {
        return "TAG|" + this.tagKey.id().toString();
    }

    @Override
    public String toString() {
        return "TAG: " + this.tagKey.id().toString();
    }
}
