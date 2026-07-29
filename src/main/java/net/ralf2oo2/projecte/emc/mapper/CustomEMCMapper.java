package net.ralf2oo2.projecte.emc.mapper;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.api.config.CustomEMCParser;
import net.ralf2oo2.projecte.emc.collector.MappingCollector;
import net.ralf2oo2.projecte.emc.json.NormalizedSimpleStack;

public class CustomEMCMapper implements EMCMapper<NormalizedSimpleStack, Long> {
    @Override
    public void addMappings(MappingCollector<NormalizedSimpleStack, Long> mapper, CommentedConfig config) {
        for (CustomEMCParser.CustomEMCEntry entry : CustomEMCParser.currentEntries.entries) {
            ProjectE.debugLog("Adding custom EMC value for {}: {}", entry.nss, entry.emc);
            mapper.setValueBefore(entry.nss, entry.emc);
        }
    }

    @Override
    public String getName() {
        return "CustomEMCMapper";
    }

    @Override
    public String getDescription() {
        return "Uses the `custom_emc.json` File to add EMC values.";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
