package net.ralf2oo2.projecte.screen.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.modificationstation.stationapi.api.block.States;
import net.ralf2oo2.projecte.block.entity.AlchemicalChestBlockEntity;
import net.ralf2oo2.projecte.util.StackUtil;

public class AlchemicalChestScreenHandler extends ScreenHandler {
    private final AlchemicalChestBlockEntity blockEntity;

    public AlchemicalChestScreenHandler(PlayerInventory playerInventory, AlchemicalChestBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        Inventory inv = blockEntity;
        //Chest Inventory
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 13; j++)
                this.addSlot(new Slot(inv, j + i * 13, 12 + j * 18, 5 + i * 18));

        //Player Inventory
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 9; j++)
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 48 + j * 18, 152 + i * 18));

        //Player Hotbar
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(playerInventory, i, 48 + i * 18, 210));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.world.getBlockState(blockEntity.x, blockEntity.y, blockEntity.z).getBlock() != States.AIR && player.getSquaredDistance(blockEntity.x + 0.5, blockEntity.y + 0.5, blockEntity.z + 0.5) <= 64.0;
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

        if (slotIndex < 104)
        {
            if (!this.mergeItem(stack, 104, this.slots.size(), true))
                return null;
            slot.markDirty();
        }
        else if (!this.mergeItem(stack, 0, 104, false))
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
}
