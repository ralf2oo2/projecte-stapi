package net.ralf2oo2.projecte.screen.handler;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.ralf2oo2.projecte.block.entity.EnergyCollectorMK3BlockEntity;
import net.ralf2oo2.projecte.emc.FuelMapper;
import net.ralf2oo2.projecte.screen.slot.GhostSlot;
import net.ralf2oo2.projecte.screen.slot.SlotPredicates;
import net.ralf2oo2.projecte.screen.slot.ValidatedSlot;
import net.ralf2oo2.projecte.util.ScreenHandlerHelper;
import net.ralf2oo2.projecte.util.StackUtil;

public class EnergyCollectorMK3ScreenHandler extends EnergyCollectorMK1ScreenHandler{
    public EnergyCollectorMK3ScreenHandler(PlayerInventory playerInventory, EnergyCollectorMK3BlockEntity collector) {
        super(playerInventory, collector);
    }

    @Override
    void initSlots(PlayerInventory playerInventory) {
        Inventory aux = blockEntity.getAux();
        Inventory main = blockEntity.getInput();

        //Klein Star Slot
        this.addSlot(new ValidatedSlot(aux, EnergyCollectorMK3BlockEntity.UPGRADING_SLOT, 158, 58, SlotPredicates.COLLECTOR_INV));

        int counter = main.size() - 1;
        //Fuel Upgrade Slot
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                this.addSlot(new ValidatedSlot(main, counter--, 18 + i * 18, 8 + j * 18, SlotPredicates.COLLECTOR_INV));

        //Upgrade Result
        this.addSlot(new ValidatedSlot(aux, EnergyCollectorMK3BlockEntity.UPGRADE_SLOT, 158, 13, SlotPredicates.COLLECTOR_INV));

        //Upgrade Target
        this.addSlot(new GhostSlot(aux, EnergyCollectorMK3BlockEntity.LOCK_SLOT, 187, 36, SlotPredicates.COLLECTOR_LOCK));

        //Player inventory
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 30 + j * 18, 84 + i * 18));

        //Player hotbar
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(playerInventory, i, 30 + i * 18, 142));
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
            if (!ScreenHandlerHelper.mergeItemStack(slots, stack, 19, 54, false))
            {
                return null;
            }
        }
        else if (slotIndex <= 54)
        {
            if (!FuelMapper.isStackFuel(stack) || FuelMapper.isStackMaxFuel(stack) || !ScreenHandlerHelper.mergeItemStack(slots, stack, 1, 16, false))
            {
                return null;
            }
        }
        else
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

        slot.onTakeItem(stack);

        return stack;
    }
}
