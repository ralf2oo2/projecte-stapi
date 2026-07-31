package net.ralf2oo2.projecte.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.ralf2oo2.projecte.util.ItemHelper;
import net.ralf2oo2.projecte.util.StackUtil;

public class CondenserLockSlot extends GhostSlot{

    public CondenserLockSlot(Inventory inv, int index, int x, int y) {
        super(inv, index, x, y, SlotPredicates.HAS_EMC);
    }

    @Override
    public void setStack(ItemStack stack) {
        if (!StackUtil.isEmpty(stack) && ItemHelper.isDamageable(stack))
        {
            stack.setDamage(0);
        }

        super.setStack(stack);
    }
}
