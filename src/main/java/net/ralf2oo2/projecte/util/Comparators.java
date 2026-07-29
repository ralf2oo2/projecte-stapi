package net.ralf2oo2.projecte.util;

import net.minecraft.item.ItemStack;

import java.util.Comparator;

public final class Comparators {
    public static final Comparator<ItemStack> ITEMSTACK_ASCENDING = (o1, o2) -> {
        if ((StackUtil.isEmpty(o1) && StackUtil.isEmpty(o2)))
        {
            return 0;
        }
        if (StackUtil.isEmpty(o1))
        {
            return 1;
        }
        if (StackUtil.isEmpty(o2))
        {
            return -1;
        }
        if (ItemHelper.areItemStacksEqualIgnoreNBT(o1, o2))
        {
            // Same item id, same meta
            return o1.count - o2.count;
        }
        else // Different id or different meta
        {
            // Different id
            if (o1.getItem() != o2.getItem())
            {
                return o1.getItem().id - o2.getItem().id;
            }
            else
            {
                // Different meta
                return o1.getDamage() - o2.getDamage();
            }

        }
    };
}
