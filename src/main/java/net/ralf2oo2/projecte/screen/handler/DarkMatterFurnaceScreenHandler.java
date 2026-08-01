package net.ralf2oo2.projecte.screen.handler;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.modificationstation.stationapi.api.recipe.FuelRegistry;
import net.modificationstation.stationapi.api.recipe.SmeltingRegistry;
import net.ralf2oo2.projecte.api.item.ItemEmc;
import net.ralf2oo2.projecte.block.entity.DarkMatterFurnaceBlockEntity;
import net.ralf2oo2.projecte.screen.slot.SlotPredicates;
import net.ralf2oo2.projecte.screen.slot.ValidatedSlot;
import net.ralf2oo2.projecte.util.ScreenHandlerHelper;
import net.ralf2oo2.projecte.util.StackUtil;

public class DarkMatterFurnaceScreenHandler extends RedMatterFurnaceScreenHandler{
    public DarkMatterFurnaceScreenHandler(PlayerInventory playerInventory, DarkMatterFurnaceBlockEntity blockEntity) {
        super(playerInventory, blockEntity);
    }

    @Override
    void initSlots(PlayerInventory invPlayer) {
        Inventory fuel = blockEntity.getFuel();
        Inventory input = blockEntity.getInput();
        Inventory output = blockEntity.getOutput();

        //Fuel Slot
        this.addSlot(new ValidatedSlot(fuel, 0, 49, 53, SlotPredicates.FURNACE_FUEL));

        //Input(0)
        this.addSlot(new ValidatedSlot(input, 0, 49, 17, SlotPredicates.SMELTABLE));

        int counter = input.size() - 1;

        //Input Storage
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 4; j++) {
                this.addSlot(new ValidatedSlot(input, counter--, 13 + i * 18, 8 + j * 18, SlotPredicates.SMELTABLE));
            }

        counter = output.size() - 1;

        //Output
        this.addSlot(new ValidatedSlot(output, counter--, 109, 35, s -> false));

        //OutputStorage
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 4; j++) {
                this.addSlot(new ValidatedSlot(output, counter--, 131 + i * 18, 8 + j * 18, s -> false));
            }

        //Player Inventory
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        //Player Hotbar
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 142));
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

        if (slotIndex <= 18)
        {
            if (!ScreenHandlerHelper.mergeItemStack(slots, stack, 19, 55, false))
            {
                return null;
            }
        }
        else
        {

            if (FuelRegistry.getFuelTime(newStack) > 0 || newStack.getItem() instanceof ItemEmc)
            {
                if (!ScreenHandlerHelper.mergeItemStack(slots, stack, 0, 1, false))
                {
                    return null;
                }
            }
            else if (!StackUtil.isEmpty(SmeltingRegistry.getResultFor(newStack)))
            {
                if (!ScreenHandlerHelper.mergeItemStack(slots, stack, 1, 10, false))
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }

        if (StackUtil.isEmpty(stack))
        {
            slot.setStack(null);
        }
        else
        {
            slot.markDirty();
        }

        return newStack;
    }
}
