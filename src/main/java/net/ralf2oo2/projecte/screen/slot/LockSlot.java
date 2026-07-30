package net.ralf2oo2.projecte.screen.slot;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.ralf2oo2.projecte.api.item.ItemEmc;
import net.ralf2oo2.projecte.screen.inventory.TransmutationInventory;
import net.ralf2oo2.projecte.util.Constants;
import net.ralf2oo2.projecte.util.EMCHelper;
import net.ralf2oo2.projecte.util.StackUtil;

public class LockSlot extends Slot {
    private final TransmutationInventory inv;

    public LockSlot(TransmutationInventory inv, int index, int x, int y) {
        super(inv, index, x, y);
        this.inv = inv;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return SlotPredicates.RELAY_INV.test(stack);
    }

    @Override
    public void setStack(ItemStack stack) {
        if (StackUtil.isEmpty(stack))
        {
            return;
        }

        super.setStack(stack);

        if (stack.getItem() instanceof ItemEmc itemEmc)
        {
            long remainEmc = Constants.TILE_MAX_EMC - inv.capability.getEmc();

            if (itemEmc.getStoredEmc(stack) >= remainEmc)
            {
                inv.addEmc(remainEmc);
                itemEmc.extractEmc(stack, remainEmc);
            }
            else
            {
                inv.addEmc(itemEmc.getStoredEmc(stack));
                itemEmc.extractEmc(stack, itemEmc.getStoredEmc(stack));
            }
        }

        if (EMCHelper.doesItemHaveEmc(stack)) {
            inv.handleKnowledge(stack.copy());
        }
    }

    @Override
    public void onTakeItem(ItemStack stack) {
        super.onTakeItem(stack);
        inv.updateClientTargets();
    }

    @Override
    public int getMaxItemCount() {
        return 1;
    }
}
