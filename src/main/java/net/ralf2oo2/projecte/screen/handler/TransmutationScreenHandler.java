package net.ralf2oo2.projecte.screen.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.ralf2oo2.projecte.listener.ItemListener;
import net.ralf2oo2.projecte.screen.inventory.TransmutationInventory;
import net.ralf2oo2.projecte.screen.slot.*;
import net.ralf2oo2.projecte.util.EMCHelper;
import net.ralf2oo2.projecte.util.ItemHelper;
import net.ralf2oo2.projecte.util.StackUtil;

import java.util.Arrays;

public class TransmutationScreenHandler extends ScreenHandler {
    public final TransmutationInventory transmutationInventory;
    private final int blocked;

    public TransmutationScreenHandler(PlayerInventory playerInventory, TransmutationInventory inventory) {
        this.transmutationInventory = inventory;

        // Transmutation Inventory
        this.addSlot(new InputSlot(transmutationInventory, 0, 43, 23));
        this.addSlot(new InputSlot(transmutationInventory, 1, 34, 41));
        this.addSlot(new InputSlot(transmutationInventory, 2, 52, 41));
        this.addSlot(new InputSlot(transmutationInventory, 3, 16, 50));
        this.addSlot(new InputSlot(transmutationInventory, 4, 70, 50));
        this.addSlot(new InputSlot(transmutationInventory, 5, 34, 59));
        this.addSlot(new InputSlot(transmutationInventory, 6, 52, 59));
        this.addSlot(new InputSlot(transmutationInventory, 7, 43, 77));
        this.addSlot(new LockSlot(transmutationInventory, 8, 158, 50));
        this.addSlot(new ConsumeSlot(transmutationInventory, 9, 107, 97));
        this.addSlot(new UnlearnSlot(transmutationInventory, 10, 89, 97));
        this.addSlot(new OutputSlot(transmutationInventory, 11, 158, 9));
        this.addSlot(new OutputSlot(transmutationInventory, 12, 176, 13));
        this.addSlot(new OutputSlot(transmutationInventory, 13, 193, 30));
        this.addSlot(new OutputSlot(transmutationInventory, 14, 199, 50));
        this.addSlot(new OutputSlot(transmutationInventory, 15, 193, 70));
        this.addSlot(new OutputSlot(transmutationInventory, 16, 176, 87));
        this.addSlot(new OutputSlot(transmutationInventory, 17, 158, 91));
        this.addSlot(new OutputSlot(transmutationInventory, 18, 140, 87));
        this.addSlot(new OutputSlot(transmutationInventory, 19, 123, 70));
        this.addSlot(new OutputSlot(transmutationInventory, 20, 116, 50));
        this.addSlot(new OutputSlot(transmutationInventory, 21, 123, 30));
        this.addSlot(new OutputSlot(transmutationInventory, 22, 140, 13));
        this.addSlot(new OutputSlot(transmutationInventory, 23, 158, 31));
        this.addSlot(new OutputSlot(transmutationInventory, 24, 177, 50));
        this.addSlot(new OutputSlot(transmutationInventory, 25, 158, 69));
        this.addSlot(new OutputSlot(transmutationInventory, 26, 139, 50));

        //Player Inventory
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 9; j++)
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 35 + j * 18, 117 + i * 18));

        //Player Hotbar
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(playerInventory, i, 35 + i * 18, 175));

        this.blocked = (this.slots.size() - 1) - (8 - playerInventory.selectedSlot);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
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

        if (slotIndex <= 7) //Input Slots
        {
            return null;
        }
        else if (slotIndex >= 11 && slotIndex <= 26) // Output Slots
        {
            long emc = EMCHelper.getEmcValue(newStack);


            long availableEmc = transmutationInventory.getAvailableEMC();

            if (availableEmc < emc) {
                return null;
            }

            int maxStackSize = newStack.getMaxCount();

            int maxAffordable = (int) Math.min(maxStackSize, availableEmc / emc);
            if (maxAffordable <= 0) {
                return null;
            }

            ItemStack toInsert = ItemHelper.getNormalizedStack(stack);
            toInsert.count = maxAffordable;

            PlayerInventory inventory = transmutationInventory.player.inventory;

            if (inventory.addStack(toInsert)) {
                long totalEmcCost = emc * maxAffordable;
                transmutationInventory.removeEmc(totalEmcCost);
            } else {
                int itemsActuallyTaken = maxAffordable - toInsert.count;
                if (itemsActuallyTaken > 0) {
                    long totalEmcCost = emc * itemsActuallyTaken;
                    transmutationInventory.removeEmc(totalEmcCost);
                }
            }

            transmutationInventory.updateClientTargets();
        }
        else if (slotIndex > 26)
        {
            long emc = EMCHelper.getEmcSellValue(stack);

            if (emc == 0 && stack.getItem() != ItemListener.tome)
            {
                return null;
            }

            while(!transmutationInventory.hasMaxedEmc() && stack.count > 0)
            {
                transmutationInventory.addEmc(emc);
                stack.count--;
            }

            transmutationInventory.handleKnowledge(newStack);

            if (StackUtil.isEmpty(stack))
            {
                slot.setStack(null);
            }
        }

        return null;
    }

    @Override
    public ItemStack onSlotClick(int index, int button, boolean shift, PlayerEntity player) {
        if (!player.world.isRemote && transmutationInventory.getInventoryForSlot(index) == transmutationInventory.outputs)
        {
            // TODO: syncing
//            PacketHandler.sendToServer(new SearchUpdatePKT(transmutationInventory.getIndexFromSlot(slot), getSlot(slot).getStack()));
        }

        if (index == blocked)
        {
            return null;
        }

        return super.onSlotClick(index, button, shift, player);
    }
}
