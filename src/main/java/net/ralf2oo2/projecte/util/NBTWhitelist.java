package net.ralf2oo2.projecte.util;

import net.minecraft.item.ItemStack;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.emc.SimpleStack;

import java.util.HashSet;
import java.util.Set;

public class NBTWhitelist {
    private static final Set<SimpleStack> STACKS = new HashSet<>();

    public static boolean register(ItemStack stack)
    {
        SimpleStack s = new SimpleStack(stack);
        return s.isValid() && STACKS.add(s.withMeta(ProjectE.WILDCARD_VALUE));
    }

    public static boolean shouldDupeWithNBT(ItemStack stack)
    {
        SimpleStack s = new SimpleStack(stack);
        return s.isValid() && STACKS.contains(s);
    }
}
