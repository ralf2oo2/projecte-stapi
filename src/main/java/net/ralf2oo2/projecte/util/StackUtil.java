package net.ralf2oo2.projecte.util;

import net.minecraft.item.ItemStack;

public class StackUtil {
    public static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.count <= 0;
    }
}
