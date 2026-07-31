package net.ralf2oo2.projecte.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.function.Predicate;

public class ValidatedSlot extends Slot {
    private final Predicate<ItemStack> validator;

    public ValidatedSlot(Inventory inventory, int index, int x, int y, Predicate<ItemStack> validator) {
        super(inventory, index, x, y);
        this.validator = validator;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return super.canInsert(stack) && validator.test(stack);
    }
}
