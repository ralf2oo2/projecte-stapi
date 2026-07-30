package net.ralf2oo2.projecte.screen.slot;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.ralf2oo2.projecte.listener.ItemListener;
import net.ralf2oo2.projecte.screen.inventory.TransmutationInventory;
import net.ralf2oo2.projecte.util.EMCHelper;
import net.ralf2oo2.projecte.util.StackUtil;

public class UnlearnSlot extends Slot {
    private final TransmutationInventory inv;

    public UnlearnSlot(TransmutationInventory inv, int index, int x, int y)
    {
        super(inv, index, x, y);
        this.inv = inv;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return !this.hasStack() && (EMCHelper.doesItemHaveEmc(stack) || stack.getItem() == ItemListener.tome);
    }

    @Override
    public void setStack(ItemStack stack) {
        if (!StackUtil.isEmpty(stack))
        {
            inv.handleUnlearn(stack.copy());
        }

        super.setStack(stack);
    }

    @Override
    public int getMaxItemCount() {
        return 1;
    }
}
