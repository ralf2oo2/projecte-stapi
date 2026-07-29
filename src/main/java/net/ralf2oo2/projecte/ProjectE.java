package net.ralf2oo2.projecte;

import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.event.EventPhases;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.init.InitFinishedEvent;
import net.modificationstation.stationapi.api.event.mod.InitEvent;
import net.modificationstation.stationapi.api.event.mod.PostInitEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;
import net.ralf2oo2.projecte.api.config.CustomEMCParser;
import net.ralf2oo2.projecte.emc.EMCMappers;
import net.ralf2oo2.projecte.emc.mapper.EMCMapper;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class ProjectE {
    public static final int WILDCARD_VALUE = Short.MAX_VALUE;

    public static File CONFIG_DIR;
    public static File PREGENERATED_EMC_FILE;

    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @Entrypoint.Logger
    public static Logger LOGGER;

    public ProjectE() {
        CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("projecte").toFile();
        PREGENERATED_EMC_FILE = new File(CONFIG_DIR, "pregenerated_emc.json");
    }

    @EventListener
    public void onInitFinished(InitFinishedEvent event) {
        long start = System.currentTimeMillis();

        CustomEMCParser.init();

        LOGGER.info("Starting server-side EMC mapping.");
        EMCMappers.map();
        LOGGER.info("Registered " + EMCMappers.emc.size() + " EMC values. (took " + (System.currentTimeMillis() - start) + " ms)");
    }

    public static void debugLog(String msg, Object... args)
    {
        if (FabricLoader.getInstance().isDevelopmentEnvironment() || false) //ProjectEConfig.misc.debugLogging
        {
            LOGGER.info(msg, args);
        } else
        {
            LOGGER.debug(msg, args);
        }
    }
}
