package net.ralf2oo2.projecte.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.List;

public class SimpleInventory implements Inventory {
    private String name;
    private int size;
    private ItemStack[] stacks;
    private List listeners;

    public SimpleInventory(String name, int size) {
        this.name = name;
        this.size = size;
        this.stacks = new ItemStack[size];
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.stacks[slot];
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (this.stacks[slot] != null) {
            if (this.stacks[slot].count <= amount) {
                ItemStack var4 = this.stacks[slot];
                this.stacks[slot] = null;
                this.markDirty();
                return var4;
            } else {
                ItemStack var3 = this.stacks[slot].split(amount);
                if (this.stacks[slot].count == 0) {
                    this.stacks[slot] = null;
                }

                this.markDirty();
                return var3;
            }
        } else {
            return null;
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.stacks[slot] = stack;
        if (stack != null && stack.count > this.getMaxCountPerStack()) {
            stack.count = this.getMaxCountPerStack();
        }

        this.markDirty();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    @Override
    public void markDirty() {
        if (this.listeners != null) {
            for(int var1 = 0; var1 < this.listeners.size(); ++var1) {
//                ((InventoryListener)this.listeners.get(var1)).onInventoryChanged(this);
            }
        }

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }
}
