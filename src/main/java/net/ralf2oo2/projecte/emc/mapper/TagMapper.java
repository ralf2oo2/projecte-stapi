package net.ralf2oo2.projecte.emc.mapper;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.common.collect.Sets;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.api.config.Configuration;
import net.ralf2oo2.projecte.emc.collector.MappingCollector;
import net.ralf2oo2.projecte.emc.json.NSSItem;
import net.ralf2oo2.projecte.emc.json.NormalizedSimpleStack;
import net.ralf2oo2.projecte.util.ConfigHelper;
import net.ralf2oo2.projecte.util.ItemHelper;
import net.ralf2oo2.projecte.util.StackUtil;

import java.util.Set;

public class TagMapper implements EMCMapper<NormalizedSimpleStack, Long>{

    private static final Set<String> BLACKLIST_EXCEPTIONS = Sets.newHashSet(
            "c:dusts/plastic"
    );

    @Override
    public void addMappings(MappingCollector<NormalizedSimpleStack, Long> mapper, CommentedConfig config) {
        if (ConfigHelper.getBoolean(config, "blacklistOresAndDusts", "", true, "Set EMC=0 for everything that has an TAG that starts with `ores` or `dusts` besides `c:dusts/plastic`")) {
            //Black-list all ores/dusts
            ItemRegistry.INSTANCE.streamTags().forEach(tagKey -> {
                Identifier id = tagKey.id();
                if (id == null) {
                    return;
                }

                if (id == Identifier.of("c:dusts") || id == Identifier.of("c:ores")) {
                    //Some exceptions in the black-listing
                    if (!BLACKLIST_EXCEPTIONS.contains(id.toString())) {
                        for (ItemStack stack : ItemHelper.getTAGItems(id.toString())) {
                            if (StackUtil.isEmpty(stack)) {
                                continue;
                            }

                            mapper.setValueBefore(NSSItem.create(stack), 0L);
                            mapper.setValueAfter(NSSItem.create(stack), 0L);
                        }
                    }
                }
            });
        }
    }

    @Override
    public String getName() {
        return "TagMapper";
    }

    @Override
    public String getDescription() {
        return "Blacklist some tags from getting an EMC value";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
