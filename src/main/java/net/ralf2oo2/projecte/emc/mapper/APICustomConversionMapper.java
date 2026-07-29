package net.ralf2oo2.projecte.emc.mapper;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.ralf2oo2.projecte.emc.collector.MappingCollector;
import net.ralf2oo2.projecte.emc.json.NormalizedSimpleStack;
import net.ralf2oo2.projecte.impl.ConversionRegistryImpl;
import net.ralf2oo2.projecte.util.ConfigHelper;

import java.util.List;
import java.util.Map;

public class APICustomConversionMapper implements EMCMapper<NormalizedSimpleStack,Long>
{
    @Override
    public String getName()
    {
        return "APICustomConversionMapper";
    }

    @Override
    public String getDescription()
    {
        return "Allows other Mods to add Recipes to the EMC Calculation.";
    }

    @Override
    public boolean isAvailable()
    {
        return true;
    }

    @Override
    public void addMappings(MappingCollector<NormalizedSimpleStack, Long> mapper, CommentedConfig config)
    {
        for (Map.Entry<String, List<ConversionRegistryImpl.APIConversion>> entry : ConversionRegistryImpl.INSTANCE.storedConversions.entrySet())
        {
            if (ConfigHelper.getBoolean(config, entry.getKey(), "allow", true,
                    String.format("Allow Mod %s to add its %d Recipes to the EMC Calculation", entry.getKey(), entry.getValue().size()))) {
                for (ConversionRegistryImpl.APIConversion apiConversion: entry.getValue()) {
                    mapper.addConversion(apiConversion.amount, apiConversion.output, apiConversion.ingredients);
                }
            }
        }

    }
}
