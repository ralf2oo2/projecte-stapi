package net.ralf2oo2.projecte.util;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public class ScreenHandlerHelper {
    public static boolean mergeItemStack(List<Slot> slots, ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        if (stack == null || stack.count <= 0) {
            return false;
        }

        boolean moved = false;
        int index = fromLast ? endIndex - 1 : startIndex;

        if (stack.isStackable()) {
            while (stack.count > 0 && ((!fromLast && index < endIndex) || (fromLast && index >= startIndex))) {
                Slot slot = slots.get(index);
                ItemStack slotStack = slot.getStack();

                if (slotStack != null && slotStack.itemId == stack.itemId
                            && (!stack.hasSubtypes() || stack.getDamage() == slotStack.getDamage())) {

                    int maxAllowed = Math.min(stack.getMaxCount(), slot.getMaxItemCount());
                    int combinedCount = slotStack.count + stack.count;

                    if (combinedCount <= maxAllowed) {
                        stack.count = 0;
                        slotStack.count = combinedCount;
                        slot.markDirty();
                        moved = true;
                    } else if (slotStack.count < maxAllowed) {
                        stack.count -= maxAllowed - slotStack.count;
                        slotStack.count = maxAllowed;
                        slot.markDirty();
                        moved = true;
                    }
                }

                if (fromLast) {
                    index--;
                } else {
                    index++;
                }
            }
        }

        if (stack.count > 0) {
            index = fromLast ? endIndex - 1 : startIndex;

            while ((!fromLast && index < endIndex) || (fromLast && index >= startIndex)) {
                Slot slot = slots.get(index);
                ItemStack slotStack = slot.getStack();

                if (slotStack == null && slot.canInsert(stack)) {
                    int maxAllowed = Math.min(stack.getMaxCount(), slot.getMaxItemCount());

                    if (stack.count <= maxAllowed) {
                        slot.setStack(stack.copy());
                        stack.count = 0;
                    } else {
                        ItemStack copy = stack.copy();
                        copy.count = maxAllowed;
                        slot.setStack(copy);
                        stack.count -= maxAllowed;
                    }

                    slot.markDirty();
                    moved = true;
                    break;
                }

                if (fromLast) {
                    index--;
                } else {
                    index++;
                }
            }
        }

        return moved;
    }
}
