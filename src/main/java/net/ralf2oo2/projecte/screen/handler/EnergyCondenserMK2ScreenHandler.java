package net.ralf2oo2.projecte.screen.handler;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.ralf2oo2.projecte.block.entity.EnergyCondenserBlockEntity;
import net.ralf2oo2.projecte.screen.slot.CondenserLockSlot;
import net.ralf2oo2.projecte.screen.slot.SlotPredicates;
import net.ralf2oo2.projecte.screen.slot.ValidatedSlot;
import net.ralf2oo2.projecte.util.EMCHelper;
import net.ralf2oo2.projecte.util.StackUtil;

public class EnergyCondenserMK2ScreenHandler extends EnergyCondenserScreenHandler{
    public EnergyCondenserMK2ScreenHandler(PlayerInventory playerInventory, EnergyCondenserBlockEntity blockEntity) {
        super(playerInventory, blockEntity);
    }

    @Override
    protected void initSlots(PlayerInventory invPlayer) {
        this.addSlot(new CondenserLockSlot(blockEntity.getLock(), 0, 12, 6));

        Inventory input = blockEntity.getInput();
        Inventory output = blockEntity.getOutput();

        //Condenser Inventory
        //Inputs
        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 6; j++)
                this.addSlot(new ValidatedSlot(input, j + i * 6, 12 + j * 18, 26 + i * 18, s -> SlotPredicates.HAS_EMC.test(s) && !blockEntity.isStackEqualToLock(s)));

        //Outputs
        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 6; j++)
                this.addSlot(new ValidatedSlot(output, j + i * 6, 138 + j * 18, 26 + i * 18, s -> false));

        //Player Inventory
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 9; j++)
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 48 + j * 18, 154 + i * 18));

        //Player Hotbar
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(invPlayer, i, 48 + i * 18, 212));
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

        if (slotIndex <= 84)
        {
            if (!this.mergeItem(stack, 85, 120, false))
            {
                return null;
            }
        }
        else {
            if (!EMCHelper.doesItemHaveEmc(stack) || blockEntity.isStackEqualToLock(stack)) {
                return null;
            }
            if(!this.mergeItem(stack, 1, 42, false)) {
                return null;
            }
        }

        if (StackUtil.isEmpty(stack))
        {
            slot.setStack(null);
        }

        else onSlotUpdate(null);
        slot.onTakeItem(stack);
        return stack;
    }
}
