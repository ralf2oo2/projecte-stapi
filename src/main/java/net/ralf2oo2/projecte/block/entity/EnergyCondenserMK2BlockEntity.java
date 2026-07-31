package net.ralf2oo2.projecte.block.entity;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.ralf2oo2.projecte.inventory.SimpleInventory;
import net.ralf2oo2.projecte.screen.slot.SlotPredicates;
import net.ralf2oo2.projecte.util.EMCHelper;
import net.ralf2oo2.projecte.util.InventoryHelper;
import net.ralf2oo2.projecte.util.ItemHelper;
import net.ralf2oo2.projecte.util.StackUtil;
import org.jetbrains.annotations.Nullable;

public class EnergyCondenserMK2BlockEntity extends EnergyCondenserBlockEntity{
    @Override
    protected Inventory createInput() {
        return new SimpleInventory("input", 42, this::markDirty);
    }

    @Override
    protected Inventory createOutput() {
        return new SimpleInventory("output", 42, this::markDirty);
    }

    @Override
    protected void condense() {
        while (this.hasSpace() && this.getStoredEmc() >= requiredEmc)
        {
            pushStack();
            this.removeEMC(requiredEmc);
        }

        if (this.hasSpace())
        {
            for (int i = 0; i < getInput().size(); i++)
            {
                ItemStack stack = getInput().getStack(i);

                if (StackUtil.isEmpty(stack))
                {
                    continue;
                }

                this.addEMC(EMCHelper.getEmcSellValue(stack) * stack.count);
                getInput().setStack(i, null);
                break;
            }
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if(tag.contains("Output")) {
            InventoryHelper.readNbtList(tag.getList("Output"), getOutput());
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.put("Output", InventoryHelper.toNbtList(getOutput()));
    }

    // ItemHandler
    @Override
    public int getItemSlots(@Nullable Direction side) {
        return 84;
    }

    @Override
    public ItemStack insertItem(ItemStack stack, int slot, @Nullable Direction side) {
        if (StackUtil.isEmpty(stack)) {
            return null;
        }

        if (slot >= 42) {
            return stack;
        }

        if (!SlotPredicates.HAS_EMC.test(stack) || isStackEqualToLock(stack)) {
            return stack;
        }

        ItemStack currentSlotStack = getInput().getStack(slot);

        if (StackUtil.isEmpty(currentSlotStack)) {
            getInput().setStack(slot, stack.copy());
            return null;
        } else if (ItemHelper.areItemStacksEqual(currentSlotStack, stack)) {
            int max = Math.min(stack.getMaxCount(), getInput().getMaxCountPerStack());
            int space = max - currentSlotStack.count;

            if (space <= 0) {
                return stack;
            }

            int toInsert = Math.min(stack.count, space);
            currentSlotStack.count += toInsert;
            getInput().markDirty();

            if (stack.count - toInsert <= 0) {
                return null;
            } else {
                ItemStack remainder = stack.copy();
                remainder.count -= toInsert;
                return remainder;
            }
        }

        return stack;
    }

    @Override
    public ItemStack insertItem(ItemStack stack, @Nullable Direction side) {
        if (StackUtil.isEmpty(stack)) {
            return null;
        }

        ItemStack remainder = stack.copy();

        for (int i = 0; i < 42; i++) {
            remainder = insertItem(remainder, i, side);
            if (StackUtil.isEmpty(remainder)) {
                return null;
            }
        }

        return remainder;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, @Nullable Direction side) {
        if (slot < 42) {
            return null;
        }

        int outputSlot = slot - 42;
        ItemStack stack = getOutput().getStack(outputSlot);

        if (StackUtil.isEmpty(stack)) {
            return null;
        }

        int extractAmount = Math.min(amount, stack.count);
        ItemStack extracted = stack.copy();
        extracted.count = extractAmount;

        stack.count -= extractAmount;
        if (stack.count <= 0) {
            getOutput().setStack(outputSlot, null);
        } else {
            getOutput().markDirty();
        }

        return extracted;
    }

    @Override
    public ItemStack getItem(int slot, @Nullable Direction side) {
        if (slot < 42) {
            return getInput().getStack(slot);
        } else if (slot < 84) {
            return getOutput().getStack(slot - 42);
        }
        return null;
    }

    @Override
    public boolean setItem(ItemStack stack, int slot, @Nullable Direction side) {
        if (slot < 42) {
            getInput().setStack(slot, stack);
            return true;
        } else if (slot < 84) {
            getOutput().setStack(slot - 42, stack);
            return true;
        }
        return false;
    }

    @Override
    public ItemStack[] getInventory(@Nullable Direction side) {
        ItemStack[] inv = new ItemStack[84];
        for (int i = 0; i < 42; i++) {
            inv[i] = getInput().getStack(i);
            inv[i + 42] = getOutput().getStack(i);
        }
        return inv;
    }
}
