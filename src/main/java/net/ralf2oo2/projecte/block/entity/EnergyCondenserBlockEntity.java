package net.ralf2oo2.projecte.block.entity;

import net.danygames2014.nyalib.item.block.ItemHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.impl.item.StationNBTSetter;
import net.ralf2oo2.projecte.api.blockentity.EmcAcceptor;
import net.ralf2oo2.projecte.inventory.SimpleInventory;
import net.ralf2oo2.projecte.screen.handler.EnergyCondenserScreenHandler;
import net.ralf2oo2.projecte.screen.slot.SlotPredicates;
import net.ralf2oo2.projecte.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EnergyCondenserBlockEntity extends EmcBlockEntity implements EmcAcceptor, ItemHandler {
    protected final Inventory inputInventory = createInput();
    private final Inventory outputInventory = createOutput();
    private final Inventory lock = new SimpleInventory("lock", 1, this::markDirty);
    private boolean isAcceptingEmc;
    public long displayEmc;
    public long requiredEmc;

    private final List<EnergyCondenserScreenHandler> openHandlers = new ArrayList<>();

    public void addOpenHandler(EnergyCondenserScreenHandler handler) {
        openHandlers.add(handler);
    }

    public void removeOpenHandler(EnergyCondenserScreenHandler handler) {
        openHandlers.remove(handler);
    }

    public Inventory getLock()
    {
        return lock;
    }

    public Inventory getInput()
    {
        return inputInventory;
    }

    public Inventory getOutput()
    {
        return outputInventory;
    }

    protected Inventory createInput()
    {
        return new SimpleInventory("input", 91, this::markDirty);
    }

    protected Inventory createOutput()
    {
        return inputInventory;
    }

    @Override
    public void tick() {

        if (this.world.isRemote)
        {
            return;
        }

        checkLockAndUpdate();

        this.displayEmc = this.getStoredEmc();

        if (!StackUtil.isEmpty(lock.getStack(0)) && requiredEmc != 0)
        {
            condense();
        }

        if (!openHandlers.isEmpty()) {
            for (EnergyCondenserScreenHandler handler : openHandlers) {
                handler.sendContentUpdates();
            }
        }
    }

    public void updateState() {
        checkLockAndUpdate();
        this.displayEmc = this.getStoredEmc();
    }

    private void checkLockAndUpdate()
    {
        if (StackUtil.isEmpty(lock.getStack(0)))
        {
            displayEmc = 0;
            requiredEmc = 0;
            this.isAcceptingEmc = false;
            return;
        }

        if (EMCHelper.doesItemHaveEmc(lock.getStack(0)))
        {
            long lockEmc = EMCHelper.getEmcValue(lock.getStack(0));

            if (requiredEmc != lockEmc)
            {
                requiredEmc = lockEmc;
                this.isAcceptingEmc = true;
            }
        }
        else
        {
            lock.setStack(0, null);

            displayEmc = 0;
            requiredEmc = 0;
            this.isAcceptingEmc = false;
        }
    }

    protected void condense()
    {
        for (int i = 0; i < inputInventory.size(); i++)
        {
            ItemStack stack = inputInventory.getStack(i);

            if (StackUtil.isEmpty(stack) || isStackEqualToLock(stack))
            {
                continue;
            }

            inputInventory.removeStack(i, 1);
            this.addEMC(EMCHelper.getEmcSellValue(stack));
            break;
        }

        if (this.getStoredEmc() >= requiredEmc && this.hasSpace())
        {
            this.removeEMC(requiredEmc);
            pushStack();
        }
    }

    protected void pushStack()
    {
        ItemStack lockCopy = lock.getStack(0).copy();

        if (!NBTWhitelist.shouldDupeWithNBT(lockCopy))
        {
            StationNBTSetter.cast(lockCopy).setStationNbt(new NbtCompound());
        }

        ItemHelper.insertItemStacked(outputInventory, lockCopy);
    }

    protected boolean hasSpace()
    {
        for (int i = 0; i < outputInventory.size(); i++)
        {
            ItemStack stack = outputInventory.getStack(i);

            if (StackUtil.isEmpty(stack))
            {
                return true;
            }

            if (isStackEqualToLock(stack) && stack.count < stack.getMaxCount())
            {
                return true;
            }
        }

        return false;
    }

    public boolean isStackEqualToLock(ItemStack stack)
    {
        if (StackUtil.isEmpty(lock.getStack(0)))
        {
            return false;
        }

        if (NBTWhitelist.shouldDupeWithNBT(lock.getStack(0)))
        {
            return ItemHelper.areItemStacksEqual(lock.getStack(0), stack);
        }

        return ItemHelper.areItemStacksEqualIgnoreNBT(lock.getStack(0), stack);
    }

    @Override
    public long acceptEMC(@NotNull Direction side, long toAccept) {
        if (isAcceptingEmc)
        {
            long toAdd = Math.min(maximumEMC - currentEMC, toAccept);
            addEMC(toAdd);
            return toAdd;
        }
        else
        {
            return 0;
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if(tag.contains("Input")) {
            InventoryHelper.readNbtList(tag.getList("Input"), inputInventory);
        }

        if(tag.contains("LockSlot")) {
            InventoryHelper.readNbtList(tag.getList("LockSlot"), lock);
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.put("Input", InventoryHelper.toNbtList(inputInventory));
        tag.put("LockSlot", InventoryHelper.toNbtList(lock));
    }

    // ItemHandler
    @Override
    public boolean canInsertItem(@Nullable Direction side) {
        return true;
    }

    @Override
    public boolean canExtractItem(@Nullable Direction side) {
        return true;
    }

    @Override
    public boolean canConnectItem(Direction side) {
        return true;
    }

    @Override
    public int getItemSlots(@Nullable Direction side) {
        return getInput().size();
    }

    @Override
    public ItemStack insertItem(ItemStack stack, int slot, @Nullable Direction side) {
        if (StackUtil.isEmpty(stack)) {
            return null;
        }

        if (!SlotPredicates.HAS_EMC.test(stack) || isStackEqualToLock(stack)) {
            return stack;
        }

        ItemStack currentSlotStack = getInput().getStack(slot);

        if (StackUtil.isEmpty(currentSlotStack)) {
            getInput().setStack(slot, stack.copy());
            return null;
        } else if (ItemHelper.areItemStacksEqual(currentSlotStack, stack)) {
            int max = Math.min(stack.getMaxCount(), getInput().getMaxCountPerStack());
            int space = max - currentSlotStack.count;

            if (space <= 0) {
                return stack;
            }

            int toInsert = Math.min(stack.count, space);
            currentSlotStack.count += toInsert;
            getInput().markDirty();

            if (stack.count - toInsert <= 0) {
                return null;
            } else {
                ItemStack remainder = stack.copy();
                remainder.count -= toInsert;
                return remainder;
            }
        }

        return stack;
    }

    @Override
    public ItemStack insertItem(ItemStack stack, @Nullable Direction side) {
        if (StackUtil.isEmpty(stack)) {
            return null;
        }

        ItemStack remainder = stack.copy();

        for (int i = 0; i < getItemSlots(side); i++) {
            remainder = insertItem(remainder, i, side);
            if (StackUtil.isEmpty(remainder)) {
                return null;
            }
        }

        return remainder;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, @Nullable Direction side) {
        ItemStack stack = getOutput().getStack(slot);

        if (StackUtil.isEmpty(stack)) {
            return null;
        }

        if (!isStackEqualToLock(stack)) {
            return null;
        }

        int extractAmount = Math.min(amount, stack.count);
        ItemStack extracted = stack.copy();
        extracted.count = extractAmount;

        stack.count -= extractAmount;
        if (stack.count <= 0) {
            getOutput().setStack(slot, null);
        } else {
            getOutput().markDirty();
        }

        return extracted;
    }

    @Override
    public ItemStack getItem(int slot, @Nullable Direction side) {
        if (slot >= 0 && slot < getInput().size()) {
            return getInput().getStack(slot);
        }
        return null;
    }

    @Override
    public boolean setItem(ItemStack stack, int slot, @Nullable Direction side) {
        if (slot >= 0 && slot < getInput().size()) {
            getInput().setStack(slot, stack);
            return true;
        }
        return false;
    }

    @Override
    public ItemStack[] getInventory(@Nullable Direction side) {
        ItemStack[] inv = new ItemStack[getInput().size()];
        for (int i = 0; i < inv.length; i++) {
            inv[i] = getInput().getStack(i);
        }
        return inv;
    }

    // Temporary override until dany fixes bug
    @Override
    public ItemStack extractItem(Item item, int meta, int amount, @Nullable Direction side) {
        ItemStack currentStack = null;
        int remaining = amount;

        for (int i = 0; i < getItemSlots(side); i++) {
            if (remaining <= 0) {
                break;
            }

            ItemStack slotStack = getItem(i, side);
            if (StackUtil.isEmpty(slotStack)) {
                continue;
            }

            if (currentStack != null) {
                if (slotStack.isItemEqual(currentStack)) {
                    ItemStack extractedStack = extractItem(i, remaining, side);

                    if (!StackUtil.isEmpty(extractedStack)) {
                        remaining -= extractedStack.count;
                        currentStack.count += extractedStack.count;
                    }
                }
            } else {
                if (slotStack.isOf(item) && (meta == -1 || slotStack.getDamage() == meta)) {
                    ItemStack extractedStack = extractItem(i, remaining, side);

                    if (!StackUtil.isEmpty(extractedStack)) {
                        remaining -= extractedStack.count;
                        currentStack = extractedStack;
                    }
                }
            }
        }

        return currentStack;
    }
}
