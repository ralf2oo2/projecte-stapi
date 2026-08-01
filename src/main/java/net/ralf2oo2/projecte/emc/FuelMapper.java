package net.ralf2oo2.projecte.emc;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.listener.BlockListener;
import net.ralf2oo2.projecte.listener.ItemListener;
import net.ralf2oo2.projecte.util.EMCHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FuelMapper {
    private static final List<SimpleStack> FUEL_MAP = new ArrayList<>();

    public static void loadMap()
    {
        FUEL_MAP.clear();

        addToMap(new ItemStack(Item.COAL, 1, 1));
        addToMap(new ItemStack(Item.REDSTONE));
        addToMap(new ItemStack(Item.COAL));
        addToMap(new ItemStack(Item.GUNPOWDER));
        addToMap(new ItemStack(Item.GLOWSTONE_DUST));
        addToMap(new ItemStack(ItemListener.alchemicalCoal, 1));
        addToMap(new ItemStack(BlockListener.alchemicalCoalBlock, 1));
        addToMap(new ItemStack(Block.GLOWSTONE));
        addToMap(new ItemStack(ItemListener.mobiusFuel, 1));
        addToMap(new ItemStack(BlockListener.mobiusFuelBlock, 1));
        addToMap(new ItemStack(ItemListener.aeternalisFuel, 1));
        addToMap(new ItemStack(BlockListener.aeternalisFuelBlock, 1));

        FUEL_MAP.sort(Comparator.comparing(EMCMappers::getEmcValue));
    }

    private static void addToMap(ItemStack stack)
    {
        if (EMCHelper.doesItemHaveEmc(stack))
        {
            addToMap(new SimpleStack(stack));
        }
    }

    public static boolean isStackFuel(ItemStack stack)
    {
        return mapContains(new SimpleStack(stack));
    }

    public static boolean isStackMaxFuel(ItemStack stack)
    {
        return FUEL_MAP.indexOf(new SimpleStack(stack)) == FUEL_MAP.size() - 1;
    }

    public static ItemStack getFuelUpgrade(ItemStack stack)
    {
        SimpleStack fuel = new SimpleStack(stack);

        int index = FUEL_MAP.indexOf(fuel);

        if (index == -1)
        {
            ProjectE.LOGGER.warn("Tried to upgrade invalid fuel: {}", stack);
            return null;
        }

        int nextIndex = index == FUEL_MAP.size() - 1 ? 0 : index + 1;

        return FUEL_MAP.get(nextIndex).toItemStack();
    }

    private static void addToMap(SimpleStack stack)
    {
        if (stack.isValid())
        {
            if (!FUEL_MAP.contains(stack))
            {
                FUEL_MAP.add(stack);
            }
        }
    }

    private static boolean mapContains(SimpleStack stack)
    {
        return stack.isValid() && FUEL_MAP.contains(stack);
    }

    /**
     * @return An immutable version of the Fuel Map
     */
    public static List<SimpleStack> getFuelMap()
    {
        return Collections.unmodifiableList(FUEL_MAP);
    }
}
