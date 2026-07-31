package net.ralf2oo2.projecte.screen.handler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.block.States;
import net.ralf2oo2.projecte.block.entity.EnergyCondenserBlockEntity;
import net.ralf2oo2.projecte.event.PlayerAttemptCondenserSetEvent;
import net.ralf2oo2.projecte.screen.slot.CondenserLockSlot;
import net.ralf2oo2.projecte.screen.slot.SlotPredicates;
import net.ralf2oo2.projecte.screen.slot.ValidatedSlot;
import net.ralf2oo2.projecte.util.Constants;
import net.ralf2oo2.projecte.util.EMCHelper;
import net.ralf2oo2.projecte.util.PacketUtil;
import net.ralf2oo2.projecte.util.StackUtil;

public class EnergyCondenserScreenHandler extends LongScreenHandler{
    protected final EnergyCondenserBlockEntity blockEntity;
    public long displayEmc;
    public long requiredEmc;

    public EnergyCondenserScreenHandler(PlayerInventory playerInventory, EnergyCondenserBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        initSlots(playerInventory);

        if(!playerInventory.player.world.isRemote) {
            blockEntity.addOpenHandler(this);
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

    protected void initSlots(PlayerInventory invPlayer)
    {
        this.addSlot(new CondenserLockSlot(blockEntity.getLock(), 0, 12, 6));

        Inventory handler = blockEntity.getInput();

        int counter = 0;
        //Condenser Inventory
        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 13; j++)
                this.addSlot(new ValidatedSlot(handler, counter++, 12 + j * 18, 26 + i * 18, s -> SlotPredicates.HAS_EMC.test(s) && !blockEntity.isStackEqualToLock(s)));

        //Player Inventory
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 9; j++)
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 48 + j * 18, 154 + i * 18));

        //Player Hotbar
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(invPlayer, i, 48 + i * 18, 212));
    }

    @Override
    public void addListener(ScreenHandlerListener listener) {
        super.addListener(listener);
        PacketUtil.sendLongPropertyUpdate(listener, this, 0, blockEntity.displayEmc);
        PacketUtil.sendLongPropertyUpdate(listener, this, 1, blockEntity.requiredEmc);
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        if (displayEmc != blockEntity.displayEmc)
        {
            for (Object listener : listeners)
            {
                PacketUtil.sendLongPropertyUpdate((ScreenHandlerListener) listener, this, 0, blockEntity.displayEmc);
            }

            displayEmc = blockEntity.displayEmc;
        }

        if (requiredEmc != blockEntity.requiredEmc)
        {
            for (Object listener : listeners)
            {
                PacketUtil.sendLongPropertyUpdate((ScreenHandlerListener) listener, this, 1, blockEntity.requiredEmc);
            }

            requiredEmc = blockEntity.requiredEmc;
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void setProperty(int id, int value) {
        switch(id)
        {
            case 0: displayEmc = value; break;
            case 1: requiredEmc = value; break;
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void setLongProperty(int id, long data) {
        switch(id)
        {
            case 0: displayEmc = data; break;
            case 1: requiredEmc = data; break;
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

        if (slotIndex <= 91)
        {
            if (!this.mergeItem(stack, 92, 127, false))
            {
                return null;
            }
        }
        else {
            if (!EMCHelper.doesItemHaveEmc(stack) || blockEntity.isStackEqualToLock(stack)) {
                return null;
            }
            if(!this.mergeItem(stack, 1, 91, false)) {
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

    protected boolean mergeItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        boolean moved = false;
        int var5 = startIndex;
        if (fromLast) {
            var5 = endIndex - 1;
        }

        if (stack.isStackable()) {
            while(stack.count > 0 && (!fromLast && var5 < endIndex || fromLast && var5 >= startIndex)) {
                Slot var6 = (Slot)this.slots.get(var5);
                ItemStack var7 = var6.getStack();
                if (var7 != null && var7.itemId == stack.itemId && (!stack.hasSubtypes() || stack.getDamage() == var7.getDamage())) {
                    int var8 = var7.count + stack.count;
                    if (var8 <= stack.getMaxCount()) {
                        stack.count = 0;
                        var7.count = var8;
                        var6.markDirty();
                        moved = true;
                    } else if (var7.count < stack.getMaxCount()) {
                        stack.count -= stack.getMaxCount() - var7.count;
                        var7.count = stack.getMaxCount();
                        var6.markDirty();
                        moved = true;
                    }
                }

                if (fromLast) {
                    --var5;
                } else {
                    ++var5;
                }
            }
        }

        if (stack.count > 0) {
            if (fromLast) {
                var5 = endIndex - 1;
            } else {
                var5 = startIndex;
            }

            while(!fromLast && var5 < endIndex || fromLast && var5 >= startIndex) {
                Slot var10 = (Slot)this.slots.get(var5);
                ItemStack var11 = var10.getStack();
                if (var11 == null) {
                    var10.setStack(stack.copy());
                    var10.markDirty();
                    stack.count = 0;
                    moved = true;
                    break;
                }

                if (fromLast) {
                    --var5;
                } else {
                    ++var5;
                }
            }
        }
        return moved;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.world.getBlockState(blockEntity.x, blockEntity.y, blockEntity.z).getBlock() != States.AIR && player.getSquaredDistance(blockEntity.x + 0.5, blockEntity.y + 0.5, blockEntity.z + 0.5) <= 64.0;
    }

    @Override
    public ItemStack onSlotClick(int index, int button, boolean shift, PlayerEntity player) {
        if (index == 0 && (!StackUtil.isEmpty(blockEntity.getLock().getStack(0)) || StationAPI.EVENT_BUS.post(new PlayerAttemptCondenserSetEvent(player, player.inventory.getCursorStack())).isCanceled()))
        {
            if (!player.world.isRemote)
            {
                blockEntity.getLock().setStack(0, null);
                this.sendContentUpdates();
            }
            return null;
        } else {
            ItemStack stack = super.onSlotClick(index, button, shift, player);
            if(!player.world.isRemote) {
                this.sendContentUpdates();
            }
            return stack;
        }
    }

    public int getProgressScaled()
    {
        if (requiredEmc == 0)
        {
            return 0;
        }

        if (displayEmc >= requiredEmc)
        {
            return Constants.MAX_CONDENSER_PROGRESS;
        }

        return (int) (Constants.MAX_CONDENSER_PROGRESS * ((double) displayEmc / requiredEmc));
    }
}
