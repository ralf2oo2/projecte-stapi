package net.ralf2oo2.projecte.screen.slot;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.ralf2oo2.projecte.listener.ItemListener;
import net.ralf2oo2.projecte.inventory.TransmutationInventory;
import net.ralf2oo2.projecte.util.EMCHelper;
import net.ralf2oo2.projecte.util.StackUtil;

public class ConsumeSlot extends Slot {
    private final TransmutationInventory inv;

    public ConsumeSlot(TransmutationInventory inv, int index, int x, int y) {
        super(inv, index, x, y);
        this.inv = inv;
    }

    @Override
    public void setStack(ItemStack stack) {
        if (StackUtil.isEmpty(stack))
        {
            return;
        }

        ItemStack cache = stack.copy();

        long toAdd = 0;

        while (!inv.hasMaxedEmc() && stack.count > 0)
        {
            toAdd += EMCHelper.getEmcSellValue(stack);
            stack.count--;
        }

        inv.addEmc(toAdd);
        this.markDirty();

        // Might fix 0 size stacks being added
        cache.count = 1;
        inv.handleKnowledge(cache);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return !inv.hasMaxedEmc() && (EMCHelper.doesItemHaveEmc(stack) || stack.getItem() == ItemListener.tome);
    }
}
