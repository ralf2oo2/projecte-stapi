package net.ralf2oo2.projecte.playerdata;

import net.minecraft.item.ItemStack;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.emc.EMCMappers;
import net.ralf2oo2.projecte.emc.SimpleStack;
import net.ralf2oo2.projecte.util.EMCHelper;
import net.ralf2oo2.projecte.util.ItemHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Transmutation {
    private static final List<ItemStack> CACHED_TOME_KNOWLEDGE = new ArrayList<>();

    public static void clearCache() {
        CACHED_TOME_KNOWLEDGE.clear();
    }

    public static void cacheFullKnowledge()
    {
        clearCache();
        for (SimpleStack stack : EMCMappers.emc.keySet())
        {
            if (!stack.isValid())
            {
                continue;
            }

            try
            {
                ItemStack s = stack.toItemStack();
                s.count = 1;

                //Apparently items can still not have EMC if they are in the EMC map.
                if (EMCHelper.doesItemHaveEmc(s) && EMCHelper.getEmcValue(s) > 0 && !ItemHelper.containsItemStack(CACHED_TOME_KNOWLEDGE, s))
                {
                    CACHED_TOME_KNOWLEDGE.add(s);
                }
            }
            catch (Exception e)
            {
                ProjectE.LOGGER.warn("Failed to cache knowledge for {}", stack);
                e.printStackTrace();
            }
        }
    }

    public static List<ItemStack> getCachedTomeKnowledge()
    {
        return Collections.unmodifiableList(CACHED_TOME_KNOWLEDGE);
    }
}
