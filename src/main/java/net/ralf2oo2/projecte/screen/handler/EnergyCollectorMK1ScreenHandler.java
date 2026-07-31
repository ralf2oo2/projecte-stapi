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
import net.ralf2oo2.projecte.block.entity.EnergyCollectorMK1BlockEntity;
import net.ralf2oo2.projecte.emc.FuelMapper;
import net.ralf2oo2.projecte.screen.slot.GhostSlot;
import net.ralf2oo2.projecte.screen.slot.SlotPredicates;
import net.ralf2oo2.projecte.screen.slot.ValidatedSlot;
import net.ralf2oo2.projecte.util.PacketUtil;
import net.ralf2oo2.projecte.util.ScreenHandlerHelper;
import net.ralf2oo2.projecte.util.StackUtil;

public class EnergyCollectorMK1ScreenHandler extends LongScreenHandler{
    final EnergyCollectorMK1BlockEntity blockEntity;
    public int sunLevel = 0;
    public long emc = 0;
    public double kleinChargeProgress = 0;
    public double fuelProgress = 0;
    public long kleinEmc = 0;

    public EnergyCollectorMK1ScreenHandler(PlayerInventory playerInventory, EnergyCollectorMK1BlockEntity collector) {
        this.blockEntity = collector;
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

    void initSlots(PlayerInventory playerInventory)
    {
        Inventory aux = blockEntity.getAux();
        Inventory main = blockEntity.getInput();

        //Klein Star Slot
        this.addSlot(new ValidatedSlot(aux, EnergyCollectorMK1BlockEntity.UPGRADING_SLOT, 124, 58, SlotPredicates.COLLECTOR_INV));

        int counter = main.size() - 1;
        //Fuel Upgrade storage
        for (int i = 0; i <= 1; i++)
            for (int j = 0; j <= 3; j++)
                this.addSlot(new ValidatedSlot(main, counter--, 20 + i * 18, 8 + j * 18, SlotPredicates.COLLECTOR_INV));

        //Upgrade Result
        this.addSlot(new ValidatedSlot(aux, EnergyCollectorMK1BlockEntity.UPGRADE_SLOT, 124, 13, SlotPredicates.COLLECTOR_INV));

        //Upgrade Target
        this.addSlot(new GhostSlot(aux, EnergyCollectorMK1BlockEntity.LOCK_SLOT, 153, 36, SlotPredicates.COLLECTOR_LOCK));

        //Player inventory
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        //Player hotbar
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
    }

    @Override
    public void addListener(ScreenHandlerListener listener) {
        super.addListener(listener);
        listener.onPropertyUpdate(this, 0, blockEntity.getSunLevel());
        PacketUtil.sendLongPropertyUpdate(listener, this, 1, blockEntity.getStoredEmc());
        listener.onPropertyUpdate(this, 2, (int) (blockEntity.getItemChargeProportion() * 8000));
        listener.onPropertyUpdate(this, 3, (int) (blockEntity.getFuelProgress() * 8000));
        PacketUtil.sendLongPropertyUpdate(listener, this, 4, blockEntity.getItemCharge());
    }

    @Override
    public ItemStack onSlotClick(int index, int button, boolean shift, PlayerEntity player) {
        if (index >= 0 && getSlot(index) instanceof GhostSlot && !StackUtil.isEmpty(getSlot(index).getStack()))
        {
            getSlot(index).setStack(null);
            return null;
        } else
        {
            return super.onSlotClick(index, button, shift, player);
        }
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        if (sunLevel != blockEntity.getSunLevel())
        {
            for (Object listener : this.listeners)
            {
                ((ScreenHandlerListener) listener).onPropertyUpdate(this, 0, blockEntity.getSunLevel());
            }

            sunLevel = blockEntity.getSunLevel();
        }

        if (emc != blockEntity.getStoredEmc())
        {
            for (Object listener : this.listeners)
            {
                PacketUtil.sendLongPropertyUpdate((ScreenHandlerListener) listener, this, 1, blockEntity.getStoredEmc());
            }

            emc = blockEntity.getStoredEmc();
        }

        if (kleinChargeProgress != blockEntity.getItemChargeProportion())
        {
            for (Object listener : this.listeners)
            {
                ((ScreenHandlerListener) listener).onPropertyUpdate(this, 2, (int) (blockEntity.getItemChargeProportion() * 8000));
            }

            kleinChargeProgress = blockEntity.getItemChargeProportion();
        }

        if (fuelProgress != blockEntity.getFuelProgress())
        {
            for (Object listener : this.listeners)
            {
                ((ScreenHandlerListener) listener).onPropertyUpdate(this, 3, (int) (blockEntity.getFuelProgress() * 8000));
            }

            fuelProgress = blockEntity.getFuelProgress();
        }

        if (kleinEmc != blockEntity.getItemCharge())
        {
            for (Object listener : this.listeners)
            {
                PacketUtil.sendLongPropertyUpdate((ScreenHandlerListener) listener, this, 1, blockEntity.getItemCharge());
            }

            kleinEmc = blockEntity.getItemCharge();
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void setProperty(int id, int value) {
        switch (id)
        {
            case 0: sunLevel = value; break;
            case 1: emc = value; break;
            case 2: kleinChargeProgress = value / 8000.0; break;
            case 3: fuelProgress = value / 8000.0; break;
            case 4: kleinEmc = value; break;
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void setLongProperty(int id, long data) {
        switch (id)
        {
            case 1: emc = data; break;
            case 4: kleinEmc = data; break;
            default: setProperty(id, (int) data);
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

        if (slotIndex <= 10)
        {
            if (!ScreenHandlerHelper.mergeItemStack(slots, stack, 11, 46, false))
            {
                return null;
            }
        }
        else if (slotIndex <= 46)
        {
            if (!FuelMapper.isStackFuel(stack) || FuelMapper.isStackMaxFuel(stack) || !ScreenHandlerHelper.mergeItemStack(slots, stack, 1, 8, false))
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

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.world.getBlockState(blockEntity.x, blockEntity.y, blockEntity.z).getBlock() != States.AIR && player.getSquaredDistance(blockEntity.x + 0.5, blockEntity.y + 0.5, blockEntity.z + 0.5) <= 64.0;
    }
}
