package net.ralf2oo2.projecte.screen.handler;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.ralf2oo2.projecte.block.entity.AntiMatterRelayMK3BlockEntity;
import net.ralf2oo2.projecte.screen.slot.SlotPredicates;
import net.ralf2oo2.projecte.screen.slot.ValidatedSlot;
import net.ralf2oo2.projecte.util.ScreenHandlerHelper;
import net.ralf2oo2.projecte.util.StackUtil;

public class AntiMatterRelayMK3ScreenHandler extends AntiMatterRelayMK1ScreenHandler {
    public AntiMatterRelayMK3ScreenHandler(PlayerInventory playerInventory, AntiMatterRelayMK3BlockEntity relay) {
        super(playerInventory, relay);
    }

    @Override
    void initSlots(PlayerInventory invPlayer) {
        Inventory input = blockEntity.getInput();
        Inventory output = blockEntity.getOutput();

        //Burn slot
        this.addSlot(new ValidatedSlot(input, 0, 104, 58, SlotPredicates.RELAY_INV));

        int counter = input.size() - 1;
        //Inventory Buffer
        for (int i = 0; i <= 3; i++)
            for (int j = 0; j <= 4; j++)
                this.addSlot(new ValidatedSlot(input, counter--, 28 + i * 18, 18 + j * 18, SlotPredicates.RELAY_INV));

        //Klein star charge
        this.addSlot(new ValidatedSlot(output, 0, 164, 58, SlotPredicates.IITEMEMC));

        //Main player inventory
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 26 + j * 18, 113 + i * 18));

        //Player hotbar
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(invPlayer, i, 26 + i * 18, 171));
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

        if (slotIndex < 22)
        {
            if (!ScreenHandlerHelper.mergeItemStack(slots, stack, 22, this.slots.size(), true))
                return null;
            slot.markDirty();
        }
        else if (!ScreenHandlerHelper.mergeItemStack(slots, stack, 0, 21, false))
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
