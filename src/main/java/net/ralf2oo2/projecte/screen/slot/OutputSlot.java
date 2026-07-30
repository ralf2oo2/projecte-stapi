package net.ralf2oo2.projecte.screen.slot;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.ralf2oo2.projecte.screen.inventory.TransmutationInventory;
import net.ralf2oo2.projecte.util.EMCHelper;

public class OutputSlot extends Slot {
    private final TransmutationInventory inv;

    public OutputSlot(TransmutationInventory inv, int index, int x, int y)
    {
        super(inv, index, x, y);
        this.inv = inv;
    }

    @Override
    public ItemStack takeStack(int amount) {
        if(!canTakeStack()) {
            return null;
        }
        ItemStack stack = getStack().copy();
        stack.count = amount;
        long emcValue = amount * EMCHelper.getEmcValue(stack);
        if (emcValue > inv.getAvailableEMC()) {
            //Requesting more emc than available
            //Container expects stacksize=0-Itemstack for 'nothing'
            stack.count = 0;
            return stack;
        }
        inv.removeEmc(emcValue);
        inv.checkForUpdates();

        return stack;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    public boolean canTakeStack() {
        return !hasStack() || EMCHelper.getEmcValue(getStack()) <= inv.getAvailableEMC();
    }
}
