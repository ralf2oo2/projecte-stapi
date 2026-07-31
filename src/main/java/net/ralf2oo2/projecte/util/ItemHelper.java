package net.ralf2oo2.projecte.util;

import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.Items;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.registry.RegistryEntry;
import net.modificationstation.stationapi.api.tag.TagKey;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.emc.json.NSSItem;

import java.util.*;

/**
 * Helpers for Inventories, ItemStacks, Items, and the Ore Dictionary
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class ItemHelper
{
    /**
     * @return True if the only aspect these stacks differ by is stack size, false if item, meta, or nbt differ.
     */
    public static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2)
    {
        return ItemStack.areEqual(getNormalizedStack(stack1), getNormalizedStack(stack2));
    }

    public static boolean areItemStacksEqualIgnoreNBT(ItemStack stack1, ItemStack stack2)
    {
        if (stack1.getItem() != stack2.getItem())
        {
            return false;
        }


        if (stack1.getDamage() == ProjectE.WILDCARD_VALUE || stack2.getDamage() == ProjectE.WILDCARD_VALUE)
        {
            return true;
        }

        return stack1.getDamage() == stack2.getDamage();
    }

    public static boolean basicAreStacksEqual(ItemStack stack1, ItemStack stack2)
    {
        return (stack1.getItem() == stack2.getItem()) && (stack1.getDamage() == stack2.getDamage());
    }

//    public static void compactInventory(IItemHandlerModifiable inventory)
//    {
//        List<ItemStack> temp = new ArrayList<>();
//        for (int i = 0; i < inventory.getSlots(); i++)
//        {
//            if (!inventory.getStackInSlot(i).isEmpty())
//            {
//                temp.add(inventory.getStackInSlot(i));
//                inventory.setStackInSlot(i, ItemStack.EMPTY);
//            }
//        }
//
//        for (ItemStack s : temp)
//        {
//            ItemHandlerHelper.insertItemStacked(inventory, s, false);
//        }
//    }

    /**
     * Compacts and sorts list of items, without regard for stack sizes
     */
    public static void compactItemListNoStacksize(List<ItemStack> list)
    {
        for (int i = 0; i < list.size(); i++)
        {
            ItemStack s = list.get(i);
            if (!StackUtil.isEmpty(s))
            {
                for (int j = i + 1; j < list.size(); j++)
                {
                    ItemStack s1 = list.get(j);
                    if (ItemHelper.areItemStacksEqual(s, s1))
                    {
                        s.count += s1.count;
                        list.set(j, null);
                    }
                }
            }
        }

        list.removeIf(Objects::isNull);
        list.sort(Comparators.ITEMSTACK_ASCENDING);
    }

    public static boolean containsItemStack(List<ItemStack> list, ItemStack toSearch)
    {
        for (ItemStack stack : list) {
            if (StackUtil.isEmpty(stack)) {
                continue;
            }

            if (stack.getItem().equals(toSearch.getItem())) {
                if (!stack.hasSubtypes() || stack.getDamage() == toSearch.getDamage()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns an ItemStack with stacksize 1.
     */
    public static ItemStack getNormalizedStack(ItemStack stack)
    {
        if(StackUtil.isEmpty(stack)) {
            return stack;
        }
        ItemStack result = stack.copy();
        result.count = 1;
        return result;
    }

    /**
     * Get a List of itemstacks from an OD name, exploding any wildcard values into their subvariants
     * TODO 1.13 tags
     */
    public static List<ItemStack> getTAGItems(String tagName)
    {
        if (tagName == null || tagName.isEmpty()) {
            return Collections.emptyList();
        }

        Identifier tagId = Identifier.of(tagName);
        TagKey<Item> tagKey = TagKey.of(ItemRegistry.KEY, tagId);

        var entryList = ItemRegistry.INSTANCE.getOrCreateEntryList(tagKey);
        if (entryList == null) {
            return Collections.emptyList();
        }

        List<ItemStack> result = new ArrayList<>();

        for (RegistryEntry<Item> entry : entryList) {
            Item item = entry.value();
            if (item == null) continue;

            Identifier itemId = ItemRegistry.INSTANCE.getId(item);

            if (itemId != null) {
                Set<Integer> variants = NSSItem.usedMetadataMap.get(itemId);

                if (variants != null && !variants.isEmpty()) {
                    for (int meta : variants) {
                        result.add(new ItemStack(item, 1, meta));
                    }
                } else {
                    result.add(new ItemStack(item, 1, 0));
                }
            }
        }

        return result;
    }

    public static boolean hasSpace(List<ItemStack> inv, ItemStack stack)
    {
        for (ItemStack invStack : inv)
        {
            if (StackUtil.isEmpty(invStack))
            {
                return true;
            }

            if (areItemStacksEqual(stack, invStack) && invStack.count < invStack.getMaxCount())
            {
                return true;
            }
        }

        return false;
    }

    public static boolean isItemRepairable(ItemStack stack)
    {
        if (stack.hasSubtypes())
        {
            return false;
        }

        if (stack.getMaxDamage() == 0 || stack.getDamage() == 0)
        {
            return false;
        }

        Item item = stack.getItem();

        if (item instanceof ShearsItem || item instanceof FlintAndSteelItem || item instanceof FishingRodItem || item instanceof BowItem)
        {
            return true;
        }

        return (item instanceof ToolItem || item instanceof SwordItem || item instanceof HoeItem || item instanceof ArmorItem);
    }

//    public static IItemHandlerModifiable immutableCopy(IItemHandler toCopy)
//    {
//        final List<ItemStack> list = new ArrayList<>(toCopy.getSlots());
//        for (int i = 0; i < toCopy.getSlots(); i++)
//        {
//            list.add(toCopy.getStackInSlot(i));
//        }
//
//        return new IItemHandlerModifiable()
//        {
//            @Override
//            public void setStackInSlot(int slot, @Nonnull ItemStack stack) {}
//
//            @Override
//            public int getSlots() {
//                return list.size();
//            }
//
//            @Nonnull
//            @Override
//            public ItemStack getStackInSlot(int slot) {
//                return list.get(slot);
//            }
//
//            @Nonnull
//            @Override
//            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
//                return stack;
//            }
//
//            @Nonnull
//            @Override
//            public ItemStack extractItem(int slot, int amount, boolean simulate) {
//                return ItemStack.EMPTY;
//            }
//
//            @Override
//            public int getSlotLimit(int slot)
//            {
//                return getStackInSlot(slot).getMaxStackSize();
//            }
//        };
//    }

    public static boolean isDamageable(ItemStack stack)
    {
        return !stack.hasSubtypes() && stack.isDamageable();
    }

//    public static boolean isOre(BlockState state)
//    {
//        if (state.getBlock() == Block.LIT_REDSTONE_ORE)
//        {
//            return true;
//        }
//        if (state.getBlock().asItem() == null)
//        {
//            return false;
//        }
//        String oreDictName = getOreDictionaryName(stateToStack(state, 1));
//        return oreDictName.startsWith("ore") || oreDictName.startsWith("denseore");
//    }

    public static BlockState stackToState(ItemStack stack)
    {
        if (stack.getItem() instanceof BlockItem blockItem)
        {
            return blockItem.getBlock().getDefaultState();
        }
        else
        {
            return null;
        }
    }
//
//    public static ItemStack stateToStack(IBlockState state, int stackSize)
//    {
//        return new ItemStack(state.getBlock(), stackSize, state.getBlock().getMetaFromState(state));
//    }
//
//    public static ItemStack stateToDroppedStack(IBlockState state, int stackSize)
//    {
//        return new ItemStack(state.getBlock(), stackSize, state.getBlock().damageDropped(state));
//    }

    public static ItemStack insertItemStacked(Inventory inventory, ItemStack stack) {
        if (inventory == null || stack == null || stack.count <= 0) {
            return stack;
        }

        int size = inventory.size();

        for (int i = 0; i < size; i++) {
            ItemStack slotStack = inventory.getStack(i);

            if (slotStack != null && slotStack.isItemEqual(stack)) {
                int maxStack = Math.min(slotStack.getMaxCount(), inventory.getMaxCountPerStack());
                int space = maxStack - slotStack.count;

                if (space > 0) {
                    int toInsert = Math.min(stack.count, space);
                    slotStack.count += toInsert;
                    stack.count -= toInsert;

                    if (stack.count <= 0) {
                        return null;
                    }
                }
            }
        }

        for (int i = 0; i < size; i++) {
            if (inventory.getStack(i) == null) {
                int maxStack = Math.min(stack.getMaxCount(), inventory.getMaxCountPerStack());
                int toInsert = Math.min(stack.count, maxStack);

                ItemStack copy = stack.copy();
                copy.count = toInsert;
                inventory.setStack(i, copy);

                stack.count -= toInsert;

                if (stack.count <= 0) {
                    return null;
                }
            }
        }

        return stack;
    }
}
