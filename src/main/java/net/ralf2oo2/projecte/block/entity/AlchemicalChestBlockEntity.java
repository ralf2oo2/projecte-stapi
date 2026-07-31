package net.ralf2oo2.projecte.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.ralf2oo2.projecte.util.InventoryHelper;

public class AlchemicalChestBlockEntity extends EmcBlockEntity implements Inventory {

    private final ItemStack[] stacks = new ItemStack[104];

    @Override
    public int size() {
        return 104;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.stacks[slot];
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (this.stacks[slot] != null) {
            if (this.stacks[slot].count <= amount) {
                ItemStack var4 = this.stacks[slot];
                this.stacks[slot] = null;
                this.markDirty();
                return var4;
            } else {
                ItemStack var3 = this.stacks[slot].split(amount);
                if (this.stacks[slot].count == 0) {
                    this.stacks[slot] = null;
                }

                this.markDirty();
                return var3;
            }
        } else {
            return null;
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.stacks[slot] = stack;
        if (stack != null && stack.count > this.getMaxCountPerStack()) {
            stack.count = this.getMaxCountPerStack();
        }

        this.markDirty();
    }

    @Override
    public String getName() {
        return "Alchemical Chest";
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if(tag.contains("items")) {
            InventoryHelper.readNbtList(tag.getList("items"), this);
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.put("items", InventoryHelper.toNbtList(this));
    }
}
