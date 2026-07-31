package net.ralf2oo2.projecte.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class WrappedInventory implements Inventory {
    private final Inventory compose;
    private final WriteMode mode;

    public WrappedInventory(Inventory compose, WriteMode mode) {
        this.compose = compose;
        this.mode = mode;
    }

    @Override
    public int size() {
        return compose.size();
    }

    @Override
    public ItemStack getStack(int slot) {
        return compose.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (mode == WriteMode.OUT || mode == WriteMode.IN_OUT) {
            return compose.removeStack(slot, amount);
        }
        return null;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (mode == WriteMode.IN || mode == WriteMode.IN_OUT) {
            compose.setStack(slot, stack);
        }
    }

    public ItemStack insertStack(int slot, ItemStack stack) {
        if (stack == null) return null;

        if (mode != WriteMode.IN && mode != WriteMode.IN_OUT) {
            return stack;
        }

        ItemStack target = compose.getStack(slot);

        if (target == null) {
            int toInsert = Math.min(stack.count, compose.getMaxCountPerStack());
            ItemStack inserted = stack.copy();
            inserted.count = toInsert;
            compose.setStack(slot, inserted);

            if (stack.count > toInsert) {
                ItemStack remainder = stack.copy();
                remainder.count -= toInsert;
                return remainder;
            }
            return null;
        } else if (target.isItemEqual(stack) && ItemStack.areEqual(target, stack)) {
            int max = Math.min(target.getMaxCount(), compose.getMaxCountPerStack());
            int space = max - target.count;

            if (space > 0) {
                int toInsert = Math.min(stack.count, space);
                target.count += toInsert;

                if (stack.count > toInsert) {
                    ItemStack remainder = stack.copy();
                    remainder.count -= toInsert;
                    return remainder;
                }
                return null;
            }
        }

        return stack;
    }

    @Override
    public String getName() {
        return compose.getName();
    }

    @Override
    public int getMaxCountPerStack() {
        return compose.getMaxCountPerStack();
    }

    @Override
    public void markDirty() {
        compose.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return compose.canPlayerUse(player);
    }

    public enum WriteMode {
        IN,
        OUT,
        IN_OUT,
        NONE
    }
}
