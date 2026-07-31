package net.ralf2oo2.projecte.screen.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.ralf2oo2.projecte.util.ItemHelper;
import net.ralf2oo2.projecte.util.StackUtil;

import java.util.function.Predicate;

public class GhostSlot extends Slot {
    private final Predicate<ItemStack> validator;

    public GhostSlot(Inventory inv, int index, int x, int y, Predicate<ItemStack> validator) {
        super(inv, index, x, y);
        this.validator = validator;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if (!StackUtil.isEmpty(stack) && validator.test(stack))
        {
            this.setStack(ItemHelper.getNormalizedStack(stack));
        }

        return false;
    }

    @Override
    public int getMaxItemCount() {
        return 1;
    }
}
