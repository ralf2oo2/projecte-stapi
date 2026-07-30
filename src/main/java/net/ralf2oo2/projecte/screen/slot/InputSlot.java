package net.ralf2oo2.projecte.screen.slot;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.ralf2oo2.projecte.api.item.ItemEmc;
import net.ralf2oo2.projecte.inventory.TransmutationInventory;
import net.ralf2oo2.projecte.util.EMCHelper;
import net.ralf2oo2.projecte.util.StackUtil;

public class InputSlot extends Slot {
    private final TransmutationInventory inv;

    public InputSlot(TransmutationInventory inv, int index, int x, int y) {
        super(inv, index, x, y);
        this.inv = inv;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return SlotPredicates.RELAY_INV.test(stack);
    }

    @Override
    public ItemStack takeStack(int amount) {
        ItemStack stack = super.takeStack(amount);
        //Decrease the size of the stack
        if (stack.getItem() instanceof ItemEmc)
        {
            //If it was an EMC storing item then check for updates,
            // so that the right hand side shows the proper items
            inv.checkForUpdates();
        }
        return stack;
    }

    @Override
    public void setStack(ItemStack stack) {
        if (StackUtil.isEmpty(stack))
        {
            return;
        }

        super.setStack(stack);

        if (stack.getItem() instanceof ItemEmc)
        {
            ItemEmc itemEmc = ((ItemEmc) stack.getItem());
            long remainingEmc = itemEmc.getMaximumEmc(stack) - itemEmc.getStoredEmc(stack);
            long availableEMC = inv.getAvailableEMC();

            if (availableEMC >= remainingEmc)
            {
                itemEmc.addEmc(stack, remainingEmc);
                inv.removeEmc(remainingEmc);
            }
            else
            {
                itemEmc.addEmc(stack, availableEMC);
                inv.removeEmc(availableEMC);
            }
        }

        if (EMCHelper.doesItemHaveEmc(stack)) {
            inv.handleKnowledge(stack.copy());
        }
    }

    @Override
    public int getMaxItemCount() {
        return 1;
    }
}
