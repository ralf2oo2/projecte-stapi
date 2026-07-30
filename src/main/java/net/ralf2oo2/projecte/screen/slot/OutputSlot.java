package net.ralf2oo2.projecte.screen.slot;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.ralf2oo2.projecte.inventory.TransmutationInventory;
import net.ralf2oo2.projecte.util.EMCHelper;

public class OutputSlot extends Slot {
    private final TransmutationInventory inv;

    public OutputSlot(TransmutationInventory inv, int index, int x, int y)
    {
        super(inv, index, x, y);
        this.inv = inv;
    }

    @Override
    public ItemStack getStack() {
        ItemStack stack = super.getStack();
        if(stack != null) {
            return stack.copy();
        }
        return null;
    }

    @Override
    public ItemStack takeStack(int amount) {
        ItemStack current = getStack();
        if (current == null || !canTakeAmount(amount)) {
            // Container logic expects stack.count = 0 when invalid/unaffordable
            if (current != null) {
                ItemStack emptyCopy = current.copy();
                emptyCopy.count = 0;
                return emptyCopy;
            }
            return null;
        }

        ItemStack result = current.copy();
        result.count = amount;

        consumeEmc(result, amount);

        return result;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    public boolean canTakeAmount(int amount) {
        ItemStack stack = getStack();
        if (stack == null || amount <= 0) {
            return false;
        }
        long totalCost = (long) amount * EMCHelper.getEmcValue(stack);
        return totalCost <= inv.getAvailableEMC();
    }

    public void consumeEmc(ItemStack stack, int amount) {
        if (stack == null || amount <= 0) {
            return;
        }
        long emcValue = (long) amount * EMCHelper.getEmcValue(stack);
        inv.removeEmc(emcValue);
        inv.checkForUpdates();
    }
}
