package net.ralf2oo2.projecte.screen.handler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.modificationstation.stationapi.api.block.States;
import net.ralf2oo2.projecte.block.entity.AntiMatterRelayMK1BlockEntity;
import net.ralf2oo2.projecte.screen.slot.SlotPredicates;
import net.ralf2oo2.projecte.screen.slot.ValidatedSlot;
import net.ralf2oo2.projecte.util.PacketUtil;
import net.ralf2oo2.projecte.util.ScreenHandlerHelper;
import net.ralf2oo2.projecte.util.StackUtil;

public class AntiMatterRelayMK1ScreenHandler extends LongScreenHandler{
    final AntiMatterRelayMK1BlockEntity blockEntity;
    public double kleinChargeProgress = 0;
    public double inputBurnProgress = 0;
    public long emc = 0;

    public AntiMatterRelayMK1ScreenHandler(PlayerInventory playerInventory, AntiMatterRelayMK1BlockEntity relay) {
        this.blockEntity = relay;
        initSlots(playerInventory);
        if(!playerInventory.player.world.isRemote) {
            this.blockEntity.addOpenHandler(this);
        }
        sendContentUpdates();
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if(!player.world.isRemote) {
            this.blockEntity.removeOpenHandler(this);
        }
    }

    void initSlots(PlayerInventory invPlayer)
    {
        Inventory input = blockEntity.getInput();
        Inventory output = blockEntity.getOutput();

        //Klein Star charge slot
        this.addSlot(new ValidatedSlot(input, 0, 67, 43, SlotPredicates.RELAY_INV));

        int counter = input.size() - 1;
        //Main Relay inventory
        for (int i = 0; i <= 1; i++)
            for (int j = 0; j <= 2; j++)
                this.addSlot(new ValidatedSlot(input, counter--, 27 + i * 18, 17 + j * 18, SlotPredicates.RELAY_INV));

        //Burning slot
        this.addSlot(new ValidatedSlot(output, 0, 127, 43, SlotPredicates.IITEMEMC));

        //Player Inventory
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 95 + i * 18));

        //Player Hotbar
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 153));
    }

    @Override
    public void addListener(ScreenHandlerListener listener) {
        super.addListener(listener);
        PacketUtil.sendLongPropertyUpdate(listener, this, 0, blockEntity.getStoredEmc());
        listener.onPropertyUpdate(this, 1, (int) (blockEntity.getItemChargeProportion() * 8000));
        listener.onPropertyUpdate(this, 2, (int) (blockEntity.getInputBurnProportion() * 8000));
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        if (emc != blockEntity.getStoredEmc())
        {
            for (Object listener : this.listeners)
            {
                PacketUtil.sendLongPropertyUpdate((ScreenHandlerListener)listener, this, 0, blockEntity.getStoredEmc());
            }

            emc = blockEntity.getStoredEmc();
        }

        if (kleinChargeProgress != blockEntity.getItemChargeProportion())
        {
            for (Object listener : this.listeners)
            {
                ((ScreenHandlerListener)listener).onPropertyUpdate(this, 1, (int) (blockEntity.getItemChargeProportion() * 8000));
            }

            kleinChargeProgress = blockEntity.getItemChargeProportion();
        }

        if (inputBurnProgress != blockEntity.getInputBurnProportion())
        {
            for (Object listener : this.listeners)
            {
                ((ScreenHandlerListener)listener).onPropertyUpdate(this, 2, (int) (blockEntity.getInputBurnProportion() * 8000));
            }

            inputBurnProgress = blockEntity.getInputBurnProportion();
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void setProperty(int id, int value) {
        switch (id)
        {
            case 0: emc = value; break;
            case 1: kleinChargeProgress = value / 8000.0; break;
            case 2: inputBurnProgress = value / 8000.0; break;
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void setLongProperty(int id, long data) {
        if (id == 0) {
            emc = data;
        } else {
            setProperty(id, (int) data);
        }
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

        if (slotIndex < 8)
        {
            if (!ScreenHandlerHelper.mergeItemStack(slots, stack, 8, this.slots.size(), true))
                return null;
            slot.markDirty();
        }
        else if (!ScreenHandlerHelper.mergeItemStack(slots, stack, 0, 7, false))
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

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.world.getBlockState(blockEntity.x, blockEntity.y, blockEntity.z).getBlock() != States.AIR && player.getSquaredDistance(blockEntity.x + 0.5, blockEntity.y + 0.5, blockEntity.z + 0.5) <= 64.0;
    }
}
