package net.ralf2oo2.projecte.emc.mapper.customconversion;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.emc.collector.MappingCollector;
import net.ralf2oo2.projecte.emc.json.NSSFake;
import net.ralf2oo2.projecte.emc.json.NSSItem;
import net.ralf2oo2.projecte.emc.json.NSSTag;
import net.ralf2oo2.projecte.emc.json.NormalizedSimpleStack;
import net.ralf2oo2.projecte.emc.mapper.EMCMapper;
import net.ralf2oo2.projecte.emc.mapper.customconversion.json.*;
import net.ralf2oo2.projecte.util.ConfigHelper;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CustomConversionMapper implements EMCMapper<NormalizedSimpleStack, Long>
{
    private static final String EXAMPLE_FILENAME = "example";
    private static final ImmutableList<String> defaultFilenames = ImmutableList.of("defaults", "tag_defaults", "metals");
    public static final Gson GSON = new GsonBuilder()
                                            .registerTypeAdapter(CustomConversion.class, new CustomConversionDeserializer())
                                            .registerTypeAdapter(FixedValues.class, new FixedValuesDeserializer())
                                            .registerTypeAdapter(NormalizedSimpleStack.class, NormalizedSimpleStack.Serializer.INSTANCE)
                                            .setPrettyPrinting()
                                            .create();

    @Override
    public String getName()
    {
        return "CustomConversionMapper";
    }

    @Override
    public String getDescription()
    {
        return "Uses json files within config/ProjectE/customConversions/ to add values and conversions";
    }

    @Override
    public boolean isAvailable()
    {
        return true;
    }

    @Override
    public void addMappings(MappingCollector<NormalizedSimpleStack, Long> mapper, CommentedConfig config)
    {
        File customConversionFolder = getCustomConversionFolder();
        if (customConversionFolder.isDirectory() || customConversionFolder.mkdir()) {
            tryToWriteDefaultFiles();

            for (String defaultFile : defaultFilenames)
            {
                readFile(new File(customConversionFolder, defaultFile + ".json"), config, mapper, true);
            }

            List<File> sortedFiles = Arrays.asList(customConversionFolder.listFiles());
            Collections.sort(sortedFiles);

            for (File f : sortedFiles)
            {
                readFile(f, config, mapper, false);
            }

            NSSFake.resetNamespace();
        } else {
            ProjectE.LOGGER.fatal("COULD NOT CREATE customConversions FOLDER IN config/ProjectE");
        }
    }

    private static void readFile(File f, CommentedConfig config, MappingCollector<NormalizedSimpleStack, Long> mapper, boolean allowDefaults)
    {
        if (f.isFile() && f.canRead() && f.getName().toLowerCase().endsWith(".json")) {
            String name = f.getName().substring(0, f.getName().length() - ".json".length());

            if (!EXAMPLE_FILENAME.equals(name)
                        && (allowDefaults || !defaultFilenames.contains(name))
                        && ConfigHelper.getBoolean(config, name, "", true, String.format("Read file: %s?", f.getName()))) {
                try
                {
                    NSSFake.setCurrentNamespace(name);
                    addMappingsFromFile(new FileReader(f), mapper);
                    ProjectE.debugLog("Collected Mappings from {}", f.getName());
                } catch (Exception e) {
                    ProjectE.LOGGER.fatal("Exception when reading file: {}", f);
                    e.printStackTrace();
                }
            }
        }

    }

    private static File getCustomConversionFolder()
    {
        return new File(ProjectE.CONFIG_DIR, "customConversions");
    }

    private static void addMappingsFromFile(Reader json, MappingCollector<NormalizedSimpleStack, Long> mapper) {
        addMappingsFromFile(parseJson(json), mapper);
    }

    private static void addMappingsFromFile(CustomConversionFile file, MappingCollector<NormalizedSimpleStack, Long> mapper) {
        //TODO implement buffered IMappingCollector to recover from failures
        for (Map.Entry<String, ConversionGroup> entry : file.groups.entrySet())
        {
            ProjectE.debugLog("Adding conversions from group '{}' with comment '{}'", entry.getKey(), entry.getValue().comment);
            try
            {
                for (CustomConversion conversion : entry.getValue().conversions)
                {
                    mapper.addConversion(conversion.count, conversion.output, conversion.ingredients);
                }
            } catch (Exception e) {
                ProjectE.LOGGER.fatal("ERROR reading custom conversion from group {}!", entry.getKey());
                e.printStackTrace();
            }
        }

        try
        {
            if (file.values.setValueBefore != null) {
                for (Map.Entry<NormalizedSimpleStack, Long> entry : file.values.setValueBefore.entrySet())
                {
                    NormalizedSimpleStack something = entry.getKey();
                    mapper.setValueBefore(something, entry.getValue());
                    if (something instanceof NSSTag nssTag)
                    {
                        for (NSSItem item : nssTag.getMembers())
                        {
                            mapper.setValueBefore(item, entry.getValue());
                        }
                    }
                }
            }
            if (file.values.setValueAfter != null)
            {
                for (Map.Entry<NormalizedSimpleStack, Long> entry : file.values.setValueAfter.entrySet())
                {
                    NormalizedSimpleStack something = entry.getKey();
                    mapper.setValueAfter(something, entry.getValue());
                    if (something instanceof NSSTag nssTag)
                    {
                        for (NSSItem item : nssTag.getMembers())
                        {
                            mapper.setValueAfter(item, entry.getValue());
                        }
                    }
                }
            }
            if (file.values.conversion != null)
            {
                for (CustomConversion conversion : file.values.conversion)
                {
                    NormalizedSimpleStack out = conversion.output;
                    if (conversion.evalTAG && out instanceof NSSTag nssTag)
                    {
                        for (NSSItem item : nssTag.getMembers())
                        {
                            mapper.setValueFromConversion(conversion.count, item, conversion.ingredients);
                        }
                    }
                    mapper.setValueFromConversion(conversion.count, out, conversion.ingredients);
                }
            }
        } catch (Exception e) {
            ProjectE.LOGGER.fatal("ERROR reading custom conversion values!");
            e.printStackTrace();
        }
    }

    public static CustomConversionFile parseJson(Reader json) {
        return GSON.fromJson(new BufferedReader(json), CustomConversionFile.class);
    }


    private static void tryToWriteDefaultFiles() {
        writeDefaultFile(EXAMPLE_FILENAME);

        for (String filename : defaultFilenames) {
            writeDefaultFile(filename);
        }
    }

    private static void writeDefaultFile(String filename) {
        File f = new File(getCustomConversionFolder(), filename + ".json");

        if (f.exists()) {
            f.delete();
        }

        try
        {
            if (f.createNewFile() && f.canWrite())
            {
                String path = "defaultCustomConversions/" + filename + ".json";
                try (InputStream stream = CustomConversionMapper.class.getClassLoader().getResourceAsStream(path);
                     OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(f)))
                {
                    IOUtils.copy(stream, outputStream);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
