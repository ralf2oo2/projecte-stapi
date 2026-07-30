package net.ralf2oo2.projecte.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.util.EMCHelper;

public class ProjectEItem extends TemplateItem {
    public static final String TAG_ACTIVE = "Active";
    public static final String TAG_MODE = "Mode";

    public ProjectEItem(Identifier identifier) {
        super(identifier);
    }

    // TODO: hook this up
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        if (oldStack.getItem() != newStack.getItem())
            return true;

        boolean diffActive = oldStack.getStationNbt().contains(TAG_ACTIVE) && newStack.getStationNbt().contains(TAG_ACTIVE)
                                     && !oldStack.getStationNbt().getCompound(TAG_ACTIVE).equals(newStack.getStationNbt().getCompound(TAG_ACTIVE));

        boolean diffMode = oldStack.getStationNbt().contains(TAG_MODE) && newStack.getStationNbt().contains(TAG_MODE)
                                   && !oldStack.getStationNbt().getCompound(TAG_MODE).equals(newStack.getStationNbt().getCompound(TAG_MODE));

        return diffActive || diffMode;
    }

    public static long getEmc(ItemStack stack)
    {
        return stack.getStationNbt().getLong("StoredEMC");
    }

    public static void setEmc(ItemStack stack, long amount)
    {
        stack.getStationNbt().putLong("StoredEMC", amount);
    }

    public static void addEmcToStack(ItemStack stack, long amount)
    {
        setEmc(stack, getEmc(stack) + amount);
    }

    public static void removeEmc(ItemStack stack, long amount)
    {
        long result = getEmc(stack) - amount;

        if (result < 0)
        {
            result = 0;
        }

        setEmc(stack, result);
    }

    public static boolean consumeFuel(PlayerEntity player, ItemStack stack, long amount, boolean shouldRemove)
    {
        if (amount <= 0)
        {
            return true;
        }

        long current = getEmc(stack);

        if (current < amount)
        {
            long consume = EMCHelper.consumePlayerFuel(player, amount - current);

            if (consume == -1)
            {
                return false;
            }

            addEmcToStack(stack, consume);
        }

        if (shouldRemove)
        {
            removeEmc(stack, amount);
        }

        return true;
    }
}
