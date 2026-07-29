package net.ralf2oo2.projecte.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class InventoryHelper {
    public static NbtList toNbtList(Inventory inventory) {
        NbtList tagList = new NbtList();
        if (inventory == null) return tagList;

        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (stack != null && stack.itemId != 0) {
                NbtCompound itemTag = new NbtCompound();
                itemTag.putByte("Slot", (byte) slot);
                stack.writeNbt(itemTag);
                tagList.add(itemTag);
            }
        }
        return tagList;
    }

    public static void readNbtList(NbtList tagList, Inventory inventory) {
        if (tagList == null || inventory == null) return;

        for (int i = 0; i < inventory.size(); i++) {
            inventory.setStack(i, null);
        }

        for (int i = 0; i < tagList.size(); i++) {
            NbtCompound itemTag = (NbtCompound) tagList.get(i);
            int slot = itemTag.getByte("Slot") & 0xFF;
            if (slot >= 0 && slot < inventory.size()) {
                inventory.setStack(slot, new ItemStack(itemTag));
            }
        }
    }

    public static SimpleInventory createFromNbtList(NbtList tagList, String name, int size) {
        SimpleInventory inv = new SimpleInventory(name, size);
        readNbtList(tagList, inv);
        return inv;
    }
}
