package net.ralf2oo2.projecte.emc.mapper;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.util.Namespace;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.emc.collector.MappingCollector;
import net.ralf2oo2.projecte.emc.json.NSSItem;
import net.ralf2oo2.projecte.emc.json.NormalizedSimpleStack;
import net.ralf2oo2.projecte.impl.ConversionRegistryImpl;
import net.ralf2oo2.projecte.util.ConfigHelper;
import net.ralf2oo2.projecte.util.StackUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class APICustomEMCMapper implements EMCMapper<NormalizedSimpleStack, Long> {
    public static final APICustomEMCMapper INSTANCE = new APICustomEMCMapper();
    private static final int PRIORITY_MIN_VALUE = 0;
    private static final int PRIORITY_MAX_VALUE = 512;
    private static final int PRIORITY_DEFAULT_VALUE = 1;
    private APICustomEMCMapper() {}

    //Need a special Map for Items and Blocks because the ItemID-mapping might change, so we need to store modid:unlocalizedName instead of the NormalizedSimpleStack which only holds itemid and metadata
    private final Map<String, Map<NormalizedSimpleStack, Long>> customEMCforMod = new HashMap<>();
    private final Map<String, Map<NormalizedSimpleStack, Long>> customNonItemEMCforMod = new HashMap<>();

    public void registerCustomEMC(@NotNull Namespace namespace, ItemStack stack, long emcValue) {
        if (StackUtil.isEmpty(stack)) return;
        if (emcValue < 0) emcValue = 0;
        String modId = namespace.toString();
        Map<NormalizedSimpleStack, Long> modMap;
        if (customEMCforMod.containsKey(modId)) {
            modMap = customEMCforMod.get(modId);
        } else {
            modMap = new HashMap<>();
            customEMCforMod.put(modId, modMap);
        }
        modMap.put(NSSItem.create(stack), emcValue);
    }

    public void registerCustomEMC(@NotNull Namespace namespace, Object o, long emcValue) {
        NormalizedSimpleStack stack = ConversionRegistryImpl.INSTANCE.objectToNSS(namespace, o);
        if (stack == null) return;
        if (emcValue < 0) emcValue = 0;
        String modId = namespace.toString();
        Map<NormalizedSimpleStack, Long> modMap;
        if (customNonItemEMCforMod.containsKey(modId)) {
            modMap = customNonItemEMCforMod.get(modId);
        } else {
            modMap = new HashMap<>();
            customNonItemEMCforMod.put(modId, modMap);
        }
        modMap.put(stack, emcValue);
    }

    @Override
    public String getName() {
        return "APICustomEMCMapper";
    }

    @Override
    public String getDescription() {
        return "Allows other mods to set EMC values using the ProjectEAPI";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void addMappings(MappingCollector<NormalizedSimpleStack, Long> mapper, CommentedConfig config) {
        Map<String, Integer> priorityMap = new HashMap<>();
        Set<String> modIdSet = new HashSet<>();
        modIdSet.addAll(customEMCforMod.keySet());
        modIdSet.addAll(customNonItemEMCforMod.keySet());

        for (String modId: modIdSet) {
            if (modId == null) continue;
            int valueCount = 0;
            if (customEMCforMod.containsKey(modId))
            {
                valueCount += customEMCforMod.get(modId).size();
            }
            if (customNonItemEMCforMod.containsKey(modId))
            {
                valueCount += customNonItemEMCforMod.get(modId).size();
            }
            priorityMap.put(modId, ConfigHelper.getClampedInt(config, modId + "priority", "customEMCPriorities", PRIORITY_DEFAULT_VALUE, PRIORITY_MIN_VALUE, PRIORITY_MAX_VALUE, "Priority for Mod with ModId = " + modId + ". Values: " + valueCount));
        }
        if (modIdSet.contains(null))
        {
            int valueCount = 0;
            if (customEMCforMod.containsKey(null))
            {
                valueCount += customEMCforMod.get(null).size();
            }
            if (customNonItemEMCforMod.containsKey(null))
            {
                valueCount += customNonItemEMCforMod.get(null).size();
            }
            priorityMap.put(null, ConfigHelper.getClampedInt(config, "modlessCustomEMCPriority", "", PRIORITY_DEFAULT_VALUE, PRIORITY_MIN_VALUE, PRIORITY_MAX_VALUE, "Priority for custom EMC values for which the ModId could not be determined. 0 to disable. Values: " + valueCount));
        }

        List<String> modIds = new ArrayList<>(modIdSet);
        modIds.sort(Comparator.comparingInt(priorityMap::get).reversed());

        for(String modId : modIds) {
            String modIdOrUnknown = modId == null ? "unknown mod" : modId;
            if (customEMCforMod.containsKey(modId))
            {
                for (Map.Entry<NormalizedSimpleStack, Long> entry : customEMCforMod.get(modId).entrySet())
                {
                    NormalizedSimpleStack normStack = entry.getKey();
                    if (isAllowedToSet(modId, normStack, entry.getValue(), config))
                    {
                        mapper.setValueBefore(normStack, entry.getValue());
                        ProjectE.debugLog("{} setting value for {} to {}", modIdOrUnknown, normStack, entry.getValue());
                    }
                    else
                    {
                        ProjectE.debugLog("Disallowed {} to set the value for {} to {}", modIdOrUnknown, normStack, entry.getValue());
                    }
                }
            }
            if (customNonItemEMCforMod.containsKey(modId))
            {
                for(Map.Entry<NormalizedSimpleStack, Long> entry: customNonItemEMCforMod.get(modId).entrySet()) {
                    NormalizedSimpleStack normStack = entry.getKey();
                    if (isAllowedToSet(modId, normStack, entry.getValue(), config))
                    {
                        mapper.setValueBefore(normStack, entry.getValue());
                        ProjectE.debugLog("{} setting value for {} to {}", modIdOrUnknown, normStack, entry.getValue());
                    }
                    else
                    {
                        ProjectE.debugLog("Disallowed {} to set the value for {} to {}", modIdOrUnknown, normStack, entry.getValue());
                    }
                }
            }
        }
    }

    private boolean isAllowedToSet(String modId, NormalizedSimpleStack stack, Long value, CommentedConfig config) {
        String itemName;
        if (stack instanceof NSSItem item)
        {
            itemName = item.itemName;
        } else {
            itemName = "IntermediateFakeItemsUsedInRecipes:";
        }
        String modForItem = itemName.substring(0, itemName.indexOf(':'));
        String permission = ConfigHelper.getString(config, modForItem,"permissions."+modId,"both", String.format("Allow '%s' to set and or remove values for '%s'. Options: [both, set, remove, none]", modId, modForItem), new String[]{"both", "set", "remove", "none"});
        if (permission.equals("both"))
        {
            return true;
        }
        if (value == 0)
        {
            return permission.equals("remove");
        }
        else
        {
            return permission.equals("set");
        }
    }
}
