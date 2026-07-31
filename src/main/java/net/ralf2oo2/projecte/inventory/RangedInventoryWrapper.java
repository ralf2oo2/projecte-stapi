package net.ralf2oo2.projecte.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class RangedInventoryWrapper implements Inventory {
    private final Inventory compose;
    private final int minSlot;
    private final int maxSlot;

    public RangedInventoryWrapper(Inventory compose, int minSlot, int maxSlot) {
        this.compose = compose;
        this.minSlot = minSlot;
        this.maxSlot = maxSlot;
    }

    @Override
    public int size() {
        return this.maxSlot - this.minSlot;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (!checkSlot(slot)) {
            return null;
        }
        return this.compose.getStack(slot + this.minSlot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (!checkSlot(slot)) {
            return null;
        }
        return this.compose.removeStack(slot + this.minSlot, amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (checkSlot(slot)) {
            this.compose.setStack(slot + this.minSlot, stack);
        }
    }

    @Override
    public String getName() {
        return this.compose.getName();
    }

    @Override
    public int getMaxCountPerStack() {
        return this.compose.getMaxCountPerStack();
    }

    @Override
    public void markDirty() {
        this.compose.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.compose.canPlayerUse(player);
    }

    private boolean checkSlot(int localSlot) {
        return localSlot >= 0 && (localSlot + this.minSlot) < this.maxSlot;
    }
}
