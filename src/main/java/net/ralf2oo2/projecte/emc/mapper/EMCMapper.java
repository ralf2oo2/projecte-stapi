package net.ralf2oo2.projecte.emc.mapper;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.ralf2oo2.projecte.emc.collector.MappingCollector;

/**
 * Interface for Classes that want to make Contributions to the EMC Mapping.
 * @param <T> The type, that is used to uniquely identify Items/Blocks/Everything
 * @param <V> The type for the EMC Value
 */
public interface EMCMapper<T, V extends Comparable<V>> {
    /**
     * A unique Name for the IEMCMapper. This is used to identify the IEMCMapper in the Configuration.
     * @return A unique Name
     */
    String getName();

    /**
     * A Description, that will be included as a Comment in the Configuration File
     * @return A <b>short</b> description
     */
    String getDescription();

    /**
     * This method is used to determine if this EMCMapper can work in the current environment.
     * If this returns {@code false} {@link #addMappings} will not be called.<br/>
     * This method will also be used to determine the default for enabling/disabling this IEMCMapper
     * @return {@code true} if you want {@link #addMappings} to be called, {@code false} otherwise.
     */
    boolean isAvailable();

    /**
     * The method that allows the IEMCMapper to contribute to the EMC Mapping. Use the methods provided by the {@link MappingCollector}.
     * <br/>
     * Use the config object to generate a useful Configuration for your IEMCMapper.
     * <br/>
     * The Configuration Object will be a {@link CommentedConfig},
     * so you can use {@code ""} (Empty String) as a Category to write into the root-Category that is created for your IEMCMapper.
     * @param mapper
     * @param config
     */
    void addMappings(MappingCollector<T, V> mapper, CommentedConfig config);
}
