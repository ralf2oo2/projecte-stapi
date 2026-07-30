package net.ralf2oo2.projecte.screen.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class CombinedInventoryWrapper implements Inventory {
    protected final Inventory[] inventories;
    private final int totalSlots;

    public CombinedInventoryWrapper(Inventory... inventories) {
        this.inventories = inventories;
        int count = 0;
        for(Inventory inv : inventories) {
            count += inv.size();
        }
        this.totalSlots = count;
    }


    @Override
    public int size() {
        return totalSlots;
    }

    @Override
    public ItemStack getStack(int slot) {
        for (Inventory inv : inventories) {
            if (slot < inv.size()) {
                return inv.getStack(slot);
            }
            slot -= inv.size();
        }
        return null;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        for (Inventory inv : inventories) {
            if (slot < inv.size()) {
                return inv.removeStack(slot, amount);
            }
            slot -= inv.size();
        }
        return null;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        for (Inventory inv : inventories) {
            if (slot < inv.size()) {
                inv.setStack(slot, stack);
                return;
            }
            slot -= inv.size();
        }
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    @Override
    public void markDirty() {
        for (Inventory inv : inventories) {
            inv.markDirty();
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }
}
