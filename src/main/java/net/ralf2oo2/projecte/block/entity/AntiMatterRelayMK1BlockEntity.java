package net.ralf2oo2.projecte.block.entity;

import net.danygames2014.nyalib.item.block.ItemHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.ralf2oo2.projecte.api.blockentity.EmcAcceptor;
import net.ralf2oo2.projecte.api.blockentity.EmcProvider;
import net.ralf2oo2.projecte.api.item.ItemEmc;
import net.ralf2oo2.projecte.inventory.SimpleInventory;
import net.ralf2oo2.projecte.screen.handler.AntiMatterRelayMK1ScreenHandler;
import net.ralf2oo2.projecte.screen.slot.SlotPredicates;
import net.ralf2oo2.projecte.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AntiMatterRelayMK1BlockEntity extends EmcBlockEntity implements EmcAcceptor, EmcProvider, ItemHandler {
    private final Inventory input;
    private final Inventory output = new SimpleInventory("output", 1);
    private final long chargeRate;
    private double bonusEMC;

    private final List<AntiMatterRelayMK1ScreenHandler> openHandlers = new ArrayList<>();

    public AntiMatterRelayMK1BlockEntity() {
        this(7, Constants.RELAY_MK1_MAX, Constants.RELAY_MK1_OUTPUT);
    }

    public AntiMatterRelayMK1BlockEntity(int sizeInv, long maxEmc, long chargeRate)
    {
        super(maxEmc);
        this.chargeRate = chargeRate;
        input = new SimpleInventory("input", sizeInv)
        {
            @Override
            public void setStack(int slot, ItemStack stack)
            {
                if(StackUtil.isEmpty(stack) || SlotPredicates.RELAY_INV.test(stack)) {
                    super.setStack(slot, stack);
                }
            }
        };
    }

    public void addOpenHandler(AntiMatterRelayMK1ScreenHandler handler) {
        openHandlers.add(handler);
    }

    public void removeOpenHandler(AntiMatterRelayMK1ScreenHandler handler) {
        openHandlers.remove(handler);
    }

    private ItemStack getCharging()
    {
        return output.getStack(0);
    }

    private ItemStack getBurn()
    {
        return input.getStack(0);
    }

    public Inventory getInput()
    {
        return input;
    }

    public Inventory getOutput()
    {
        return output;
    }

    @Override
    public void tick() {
        if (world.isRemote)
        {
            return;
        }

        sendEmc();
        ItemHelper.compactInventory(input);

        ItemStack stack = getBurn();

        if (!StackUtil.isEmpty(stack))
        {
            if(stack.getItem() instanceof ItemEmc itemEmc)
            {
                long emcVal = itemEmc.getStoredEmc(stack);

                if (emcVal > chargeRate)
                {
                    emcVal = chargeRate;
                }

                if (emcVal > 0 && this.getStoredEmc() + emcVal <= this.getMaximumEmc())
                {
                    this.addEMC(emcVal);
                    itemEmc.extractEmc(stack, emcVal);
                }
            }
            else
            {
                long emcVal = EMCHelper.getEmcSellValue(stack);

                if (emcVal > 0 && (this.getStoredEmc() + emcVal) <= this.getMaximumEmc())
                {
                    this.addEMC(emcVal);
                    getBurn().count--;
                    if(getBurn().count <= 0) {
                        input.setStack(0, null);
                    }
                }
            }
        }

        ItemStack chargeable = getCharging();

        if (!StackUtil.isEmpty(chargeable) && this.getStoredEmc() > 0 && chargeable.getItem() instanceof ItemEmc)
        {
            chargeItem(chargeable);
        }

        if (!openHandlers.isEmpty()) {
            for (AntiMatterRelayMK1ScreenHandler handler : openHandlers) {
                handler.sendContentUpdates();
            }
        }
    }

    private void sendEmc()
    {
        if (this.getStoredEmc() == 0) return;

        if (this.getStoredEmc() <= chargeRate)
        {
            this.sendToAllAcceptors(this.getStoredEmc());
        }
        else
        {
            this.sendToAllAcceptors(chargeRate);
        }
    }

    private void chargeItem(ItemStack chargeable)
    {
        ItemEmc itemEmc = ((ItemEmc) chargeable.getItem());
        long starEmc = itemEmc.getStoredEmc(chargeable);
        long maxStarEmc = itemEmc.getMaximumEmc(chargeable);
        long toSend = this.getStoredEmc() < chargeRate ? this.getStoredEmc() : chargeRate;

        if ((starEmc + toSend) <= maxStarEmc)
        {
            itemEmc.addEmc(chargeable, toSend);
            this.removeEMC(toSend);
        }
        else
        {
            toSend = maxStarEmc - starEmc;
            itemEmc.addEmc(chargeable, toSend);
            this.removeEMC(toSend);
        }
    }

    public double getItemChargeProportion()
    {
        if (!StackUtil.isEmpty(getCharging()) && getCharging().getItem() instanceof ItemEmc itemEmc)
        {
            return (double) itemEmc.getStoredEmc(getCharging()) / itemEmc.getMaximumEmc(getCharging());
        }

        return 0;
    }

    public double getInputBurnProportion()
    {
        if (StackUtil.isEmpty(getBurn()))
        {
            return 0;
        }

        if (getBurn().getItem() instanceof ItemEmc itemEmc)
        {
            return (double) itemEmc.getStoredEmc(getBurn()) / itemEmc.getMaximumEmc(getBurn());
        }

        return getBurn().count / (double) getBurn().getMaxCount();
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if(tag.contains("Input")) {
            InventoryHelper.readNbtList(tag.getList("Input"), input);
        }

        if(tag.contains("Output")) {
            InventoryHelper.readNbtList(tag.getList("Output"), input);
        }

        bonusEMC = tag.getDouble("BonusEMC");
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.put("Input", InventoryHelper.toNbtList(input));
        tag.put("Output", InventoryHelper.toNbtList(output));
        tag.putDouble("BonusEMC", bonusEMC);
    }

    @Override
    public long acceptEMC(@NotNull Direction side, long toAccept) {
        BlockPos pos = new BlockPos(x, y, z).offset(side);
        if (world.getBlockEntity(pos.x, pos.y, pos.z) instanceof AntiMatterRelayMK1BlockEntity)
        {
            return 0; // Do not accept from other relays - avoid infinite loop / thrashing
        }
        else
        {
            long toAdd = Math.min(maximumEMC - currentEMC, toAccept);
            currentEMC += toAdd;
            return toAdd;
        }
    }

    public void addBonus(@NotNull Direction side, double bonus) {
        BlockPos pos = new BlockPos(x, y, z).offset(side);
        if (world.getBlockEntity(pos.x, pos.y, pos.z) instanceof AntiMatterRelayMK1BlockEntity)
        {
            return; // Do not accept from other relays - avoid infinite loop / thrashing
        }
        bonusEMC += bonus;
        if (bonusEMC >= 1) {
            long extraEMC = (long) bonusEMC;
            bonusEMC -= extraEMC;
            currentEMC += Math.min(maximumEMC - currentEMC, extraEMC);
        }
    }

    @Override
    public long provideEMC(@NotNull Direction side, long toExtract) {
        long toRemove = Math.min(currentEMC, toExtract);
        currentEMC -= toRemove;
        return toRemove;
    }

    // ItemHandler
    @Override
    public boolean canInsertItem(@Nullable Direction side) {
        return true;
    }

    @Override
    public boolean canExtractItem(@Nullable Direction side) {
        return side == Direction.DOWN;
    }

    @Override
    public boolean canConnectItem(Direction side) {
        return true;
    }

    @Override
    public int getItemSlots(@Nullable Direction side) {
        return side == Direction.DOWN ? getOutput().size() : getInput().size();
    }

    @Override
    public ItemStack insertItem(ItemStack stack, int slot, @Nullable Direction side) {
        if (StackUtil.isEmpty(stack)) {
            return null;
        }

        if (side == Direction.DOWN) {
            if (!SlotPredicates.IITEMEMC.test(stack)) {
                return stack;
            }

            ItemStack currentSlotStack = getOutput().getStack(slot);
            if (StackUtil.isEmpty(currentSlotStack)) {
                getOutput().setStack(slot, stack.copy());
                return null;
            } else if (ItemHelper.areItemStacksEqual(currentSlotStack, stack)) {
                int max = Math.min(stack.getMaxCount(), getOutput().getMaxCountPerStack());
                int space = max - currentSlotStack.count;
                if (space <= 0) return stack;

                int toInsert = Math.min(stack.count, space);
                currentSlotStack.count += toInsert;
                getOutput().markDirty();

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
        else {
            if (!SlotPredicates.RELAY_INV.test(stack)) {
                return stack;
            }

            ItemStack currentSlotStack = getInput().getStack(slot);
            if (StackUtil.isEmpty(currentSlotStack)) {
                getInput().setStack(slot, stack.copy());
                return null;
            } else if (ItemHelper.areItemStacksEqual(currentSlotStack, stack)) {
                int max = Math.min(stack.getMaxCount(), getInput().getMaxCountPerStack());
                int space = max - currentSlotStack.count;
                if (space <= 0) return stack;

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
        if (side != Direction.DOWN) {
            return null;
        }

        ItemStack stack = getOutput().getStack(slot);
        if (StackUtil.isEmpty(stack)) {
            return null;
        }

        if (stack.getItem() instanceof ItemEmc itemEmc) {
            if (itemEmc.getStoredEmc(stack) < itemEmc.getMaximumEmc(stack)) {
                return null;
            }
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
        Inventory target = (side == Direction.DOWN) ? getOutput() : getInput();
        if (slot >= 0 && slot < target.size()) {
            return target.getStack(slot);
        }
        return null;
    }

    @Override
    public boolean setItem(ItemStack stack, int slot, @Nullable Direction side) {
        Inventory target = (side == Direction.DOWN) ? getOutput() : getInput();
        if (slot >= 0 && slot < target.size()) {
            target.setStack(slot, stack);
            return true;
        }
        return false;
    }

    @Override
    public ItemStack[] getInventory(@Nullable Direction side) {
        Inventory target = (side == Direction.DOWN) ? getOutput() : getInput();
        ItemStack[] inv = new ItemStack[target.size()];
        for (int i = 0; i < inv.length; i++) {
            inv[i] = target.getStack(i);
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
