package net.ralf2oo2.projecte.emc;

import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.api.config.Configuration;
import net.ralf2oo2.projecte.api.config.PrefixConfiguration;
import net.ralf2oo2.projecte.config.Config;
import net.ralf2oo2.projecte.emc.arithmetic.HiddenBigFractionArithmetic;
import net.ralf2oo2.projecte.emc.arithmetic.ValueArithmetic;
import net.ralf2oo2.projecte.emc.collector.DumpToFileCollector;
import net.ralf2oo2.projecte.emc.collector.ExtendedMappingCollector;
import net.ralf2oo2.projecte.emc.collector.LongToBigFractionCollector;
import net.ralf2oo2.projecte.emc.collector.WildcardSetValueFixCollector;
import net.ralf2oo2.projecte.emc.generator.BigFractionToLongGenerator;
import net.ralf2oo2.projecte.emc.generator.ValueGenerator;
import net.ralf2oo2.projecte.emc.json.NSSItem;
import net.ralf2oo2.projecte.emc.json.NormalizedSimpleStack;
import net.ralf2oo2.projecte.emc.mapper.*;
import net.ralf2oo2.projecte.emc.mapper.customconversion.CustomConversionMapper;
import net.ralf2oo2.projecte.emc.pregenerated.PregeneratedEMC;
import org.apache.commons.math3.fraction.BigFraction;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class EMCMappers
{
    public static final Map<SimpleStack, Long> emc = new LinkedHashMap<>();

    public static double covalenceLoss = Config.DIFFICULTY_CONFIG.covalenceLoss;
    public static boolean covalenceLossRounding = Config.DIFFICULTY_CONFIG.covalenceLossRounding;

    public static void map()
    {
        List<EMCMapper<NormalizedSimpleStack, Long>> emcMappers = Arrays.asList(
                new TagMapper(),
//                APICustomEMCMapper.instance,
                new CustomConversionMapper(),
                new CustomEMCMapper(),
                new CraftingMapper(),
//                new moze_intel.projecte.emc.mappers.FluidMapper(),
                new SmeltingMapper()
//                new APICustomConversionMapper()
        );
        SimpleGraphMapper<NormalizedSimpleStack, BigFraction, ValueArithmetic<BigFraction>> mapper = new SimpleGraphMapper<>(new HiddenBigFractionArithmetic());
        ValueGenerator<NormalizedSimpleStack, Long> valueGenerator = new BigFractionToLongGenerator<>(mapper);
        ExtendedMappingCollector<NormalizedSimpleStack, Long, ValueArithmetic<BigFraction>> mappingCollector = new LongToBigFractionCollector<>(mapper);
        mappingCollector = new WildcardSetValueFixCollector<>(mappingCollector);

        Configuration config = new Configuration(new File(ProjectE.CONFIG_DIR, "mapping.cfg"));
        config.load();

        if (config.getBoolean("dumpEverythingToFile", "general", false,"Want to take a look at the internals of EMC Calculation? Enable this to write all the conversions and setValue-Commands to config/ProjectE/mappingdump.json")) {
            mappingCollector = new DumpToFileCollector<>(new File(ProjectE.CONFIG_DIR, "mappingdump.json"), mappingCollector);
        }

        boolean shouldUsePregenerated = config.getBoolean("pregenerate", "general", false, "When the next EMC mapping occurs write the results to config/ProjectE/pregenerated_emc.json and only ever run the mapping again" +
                                                                                                   " when that file does not exist, this setting is set to false, or an error occurred parsing that file.");

        Map<NormalizedSimpleStack, Long> graphMapperValues;
        if (shouldUsePregenerated && ProjectE.PREGENERATED_EMC_FILE.canRead() && PregeneratedEMC.tryRead(ProjectE.PREGENERATED_EMC_FILE, graphMapperValues = new HashMap<>()))
        {
            ProjectE.LOGGER.info(String.format("Loaded %d values from pregenerated EMC File", graphMapperValues.size()));
        }
        else
        {


            SimpleGraphMapper.setLogFoundExploits(config.getBoolean("logEMCExploits", "general", true,
                    "Log known EMC Exploits. This can not and will not find all possible exploits. " +
                            "This will only find exploits that result in fixed/custom emc values that the algorithm did not overwrite. " +
                            "Exploits that derive from conversions that are unknown to ProjectE will not be found."
            ));

            ProjectE.debugLog("Starting to collect Mappings...");
            for (EMCMapper<NormalizedSimpleStack, Long> emcMapper : emcMappers)
            {
                try
                {
                    if (config.getBoolean(emcMapper.getName(), "enabledMappers", emcMapper.isAvailable(), emcMapper.getDescription()) && emcMapper.isAvailable())
                    {
                        DumpToFileCollector.currentGroupName = emcMapper.getName();
                        emcMapper.addMappings(mappingCollector, new PrefixConfiguration(config, "mapperConfigurations." + emcMapper.getName()));
                        ProjectE.debugLog("Collected Mappings from " + emcMapper.getClass().getName());
                    }
                } catch (Exception e)
                {
                    ProjectE.LOGGER.fatal("Exception during Mapping Collection from Mapper {}. PLEASE REPORT THIS! EMC VALUES MIGHT BE INCONSISTENT!", emcMapper.getClass().getName());
                    e.printStackTrace();
                }
            }
            DumpToFileCollector.currentGroupName = "NSSHelper";
            NormalizedSimpleStack.addMappings(mappingCollector);

            ProjectE.debugLog("Mapping Collection finished");
            mappingCollector.finishCollection();

            ProjectE.debugLog("Starting to generate Values:");

            config.save();

            graphMapperValues = valueGenerator.generateValues();
            ProjectE.debugLog("Generated Values...");

            filterEMCMap(graphMapperValues);

            if (shouldUsePregenerated) {
                //Should have used pregenerated, but the file was not read => regenerate.
                try
                {
                    PregeneratedEMC.write(ProjectE.PREGENERATED_EMC_FILE, graphMapperValues);
                    ProjectE.debugLog("Wrote Pregen-file!");
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }


        for (Map.Entry<NormalizedSimpleStack, Long> entry: graphMapperValues.entrySet()) {
            NSSItem normStackItem = (NSSItem)entry.getKey();
            Item obj = ItemRegistry.INSTANCE.get(Identifier.of(normStackItem.itemName));
            if (obj != null)
            {
                emc.put(new SimpleStack(Identifier.of(normStackItem.itemName), normStackItem.damage), entry.getValue());
            } else {
                ProjectE.LOGGER.warn("Could not add EMC value for {}|{}. Can not get ItemID!", normStackItem.itemName, normStackItem.damage);
            }
        }

//        MinecraftForge.EVENT_BUS.post(new EMCRemapEvent());
//        Transmutation.cacheFullKnowledge();
//        FuelMapper.loadMap();
//        ProjectE.refreshJEI();
    }

    private static void filterEMCMap(Map<NormalizedSimpleStack, Long> map) {
        map.entrySet().removeIf(e -> !(e.getKey() instanceof NSSItem)
                                             || ((NSSItem) e.getKey()).damage == ProjectE.WILDCARD_VALUE
                                             || e.getValue() <= 0);
    }

    public static boolean mapContains(SimpleStack key)
    {
        return emc.containsKey(key);
    }

    public static long getEmcValue(SimpleStack stack)
    {
        return emc.get(stack);
    }

    public static void clearMaps() {
        emc.clear();
    }
}

