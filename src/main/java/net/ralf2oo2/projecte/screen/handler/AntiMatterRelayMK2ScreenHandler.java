package net.ralf2oo2.projecte.screen.handler;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.ralf2oo2.projecte.block.entity.AntiMatterRelayMK2BlockEntity;
import net.ralf2oo2.projecte.screen.slot.SlotPredicates;
import net.ralf2oo2.projecte.screen.slot.ValidatedSlot;
import net.ralf2oo2.projecte.util.ScreenHandlerHelper;
import net.ralf2oo2.projecte.util.StackUtil;

public class AntiMatterRelayMK2ScreenHandler extends AntiMatterRelayMK1ScreenHandler {
    public AntiMatterRelayMK2ScreenHandler(PlayerInventory playerInventory, AntiMatterRelayMK2BlockEntity relay) {
        super(playerInventory, relay);
    }

    @Override
    void initSlots(PlayerInventory invPlayer) {
        Inventory input = blockEntity.getInput();
        Inventory output = blockEntity.getOutput();

        //Burn slot
        this.addSlot(new ValidatedSlot(input, 0, 84, 44, SlotPredicates.RELAY_INV));

        int counter = input.size() - 1;
        //Inventory buffer
        for (int i = 0; i <= 2; i++)
            for (int j = 0; j <= 3; j++)
                this.addSlot(new ValidatedSlot(input, counter--, 26 + i * 18, 18 + j * 18, SlotPredicates.RELAY_INV));

        //Klein star slot
        this.addSlot(new ValidatedSlot(output, 0, 144, 44, SlotPredicates.IITEMEMC));

        //Main player inventory
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 16 + j * 18, 101 + i * 18));

        //Player hotbar
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(invPlayer, i, 16 + i * 18, 159));
    }

    @Override
    public ItemStack quickMove(int slotIndex) {
        Slot slot = this.getSlot(slotIndex);

        if (slot == null || !slot.hasStack())
        {
            return null;
        }

        ItemStack stack = slot.getStack();
        ItemStack newStack = stack.copy();

        if (slotIndex < 14)
        {
            if (!ScreenHandlerHelper.mergeItemStack(slots, stack, 14, this.slots.size(), true))
                return null;
            slot.markDirty();
        }
        else if (!ScreenHandlerHelper.mergeItemStack(slots, stack, 0, 13, false))
        {
            return null;
        }
        if (StackUtil.isEmpty(stack))
        {
            slot.setStack(null);
        }
        else
        {
            slot.markDirty();
        }

        slot.onTakeItem(newStack);

        return newStack;
    }
}
