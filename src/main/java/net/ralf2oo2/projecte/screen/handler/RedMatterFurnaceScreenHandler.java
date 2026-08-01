package net.ralf2oo2.projecte.screen.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.recipe.FuelRegistry;
import net.modificationstation.stationapi.api.recipe.SmeltingRegistry;
import net.ralf2oo2.projecte.api.item.ItemEmc;
import net.ralf2oo2.projecte.block.entity.RedMatterFurnaceBlockEntity;
import net.ralf2oo2.projecte.screen.slot.SlotPredicates;
import net.ralf2oo2.projecte.screen.slot.ValidatedSlot;
import net.ralf2oo2.projecte.util.ScreenHandlerHelper;
import net.ralf2oo2.projecte.util.StackUtil;

public class RedMatterFurnaceScreenHandler extends ScreenHandler {
    final RedMatterFurnaceBlockEntity blockEntity;
    private int lastCookTime;
    private int lastBurnTime;
    private int lastItemBurnTime;

    public RedMatterFurnaceScreenHandler(PlayerInventory playerInventory, RedMatterFurnaceBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        initSlots(playerInventory);
        if(!playerInventory.player.world.isRemote) {
            blockEntity.addOpenHandler(this);
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if(!player.world.isRemote) {
            blockEntity.removeOpenHandler(this);
        }
    }

    void initSlots(PlayerInventory invPlayer) {
        Inventory fuel = blockEntity.getFuel();
        Inventory input = blockEntity.getInput();
        Inventory output = blockEntity.getOutput();

        //Fuel
        this.addSlot(new ValidatedSlot(fuel, 0, 65, 53, SlotPredicates.FURNACE_FUEL));

        //Input(0)
        this.addSlot(new ValidatedSlot(input, 0, 65, 17, SlotPredicates.SMELTABLE));

        int counter = input.size() - 1;

        //Input storage
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 4; j++)
                this.addSlot(new ValidatedSlot(input, counter--, 11 + i * 18, 8 + j * 18, SlotPredicates.SMELTABLE));

        counter = output.size() - 1;

        //Output(0)
        this.addSlot(new ValidatedSlot(output, counter--, 125, 35, s -> false));

        //Output Storage
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 4; j++)
                this.addSlot(new ValidatedSlot(output, counter--, 147 + i * 18, 8 + j * 18, s -> false));

        //Player Inventory
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 24 + j * 18, 84 + i * 18));

        //Player HotBar
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(invPlayer, i, 24 + i * 18, 142));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.world.getBlockState(blockEntity.x, blockEntity.y, blockEntity.z).getBlock() != States.AIR && player.getSquaredDistance(blockEntity.x + 0.5, blockEntity.y + 0.5, blockEntity.z + 0.5) <= 64.0;
    }

    @Override
    public void addListener(ScreenHandlerListener listener) {
        super.addListener(listener);
        listener.onPropertyUpdate(this, 0, blockEntity.furnaceCookTime);
        listener.onPropertyUpdate(this, 1, blockEntity.furnaceBurnTime);
        listener.onPropertyUpdate(this, 2, blockEntity.currentItemBurnTime);
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        for (Object listener : this.listeners)
        {
            if (lastCookTime != blockEntity.furnaceCookTime)
                ((ScreenHandlerListener)listener).onPropertyUpdate(this, 0, blockEntity.furnaceCookTime);

            if (lastBurnTime != blockEntity.furnaceBurnTime)
                ((ScreenHandlerListener)listener).onPropertyUpdate(this, 1, blockEntity.furnaceBurnTime);

            if (lastItemBurnTime != blockEntity.currentItemBurnTime)
                ((ScreenHandlerListener)listener).onPropertyUpdate(this, 2, blockEntity.currentItemBurnTime);
        }

        lastCookTime = blockEntity.furnaceCookTime;
        lastBurnTime = blockEntity.furnaceBurnTime;
        lastItemBurnTime = blockEntity.currentItemBurnTime;
    }

    @Override
    public void setProperty(int id, int value) {
        if (id == 0)
            blockEntity.furnaceCookTime = value;

        if (id == 1)
            blockEntity.furnaceBurnTime = value;

        if (id == 2)
            blockEntity.currentItemBurnTime = value;
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

        if (slotIndex <= 26)
        {
            if (!ScreenHandlerHelper.mergeItemStack(slots, stack, 27, 63, false))
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
                if (!ScreenHandlerHelper.mergeItemStack(slots, stack, 1, 14, false))
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
