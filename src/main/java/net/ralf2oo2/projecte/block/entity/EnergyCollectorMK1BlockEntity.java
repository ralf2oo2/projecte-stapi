package net.ralf2oo2.projecte.block.entity;

import net.danygames2014.nyalib.item.block.ItemHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.ralf2oo2.projecte.api.blockentity.EmcAcceptor;
import net.ralf2oo2.projecte.api.blockentity.EmcProvider;
import net.ralf2oo2.projecte.api.item.ItemEmc;
import net.ralf2oo2.projecte.emc.FuelMapper;
import net.ralf2oo2.projecte.inventory.CombinedInventoryWrapper;
import net.ralf2oo2.projecte.inventory.RangedInventoryWrapper;
import net.ralf2oo2.projecte.inventory.SimpleInventory;
import net.ralf2oo2.projecte.screen.handler.EnergyCollectorMK1ScreenHandler;
import net.ralf2oo2.projecte.screen.slot.SlotPredicates;
import net.ralf2oo2.projecte.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnergyCollectorMK1BlockEntity extends EmcBlockEntity implements EmcProvider, EmcAcceptor, ItemHandler {
    private final Inventory input = new SimpleInventory("input", getInvSize());
    private final Inventory auxSlots = new SimpleInventory("auxSlots", 3);
    private final Inventory toSort = new CombinedInventoryWrapper(new RangedInventoryWrapper(auxSlots, UPGRADING_SLOT, UPGRADING_SLOT + 1), input);

    public static final int UPGRADING_SLOT = 0;
    public static final int UPGRADE_SLOT = 1;
    public static final int LOCK_SLOT = 2;

    private final long emcGen;
    private boolean hasChargeableItem;
    private boolean hasFuel;
    private long storedFuelEmc;
    private double unprocessedEMC;

    private final List<EnergyCollectorMK1ScreenHandler> openHandlers = new ArrayList<>();

    public EnergyCollectorMK1BlockEntity() {
        super(Constants.COLLECTOR_MK1_MAX);
        emcGen = Constants.COLLECTOR_MK1_GEN;
    }

    public EnergyCollectorMK1BlockEntity(long maxEmc, long emcGen)
    {
        super(maxEmc);
        this.emcGen = emcGen;
    }

    public void addOpenHandler(EnergyCollectorMK1ScreenHandler handler) {
        openHandlers.add(handler);
    }

    public void removeOpenHandler(EnergyCollectorMK1ScreenHandler handler) {
        openHandlers.remove(handler);
    }

    public Inventory getInput()
    {
        return input;
    }

    public Inventory getAux()
    {
        return auxSlots;
    }

    protected int getInvSize()
    {
        return 8;
    }

    private ItemStack getUpgraded()
    {
        return auxSlots.getStack(UPGRADE_SLOT);
    }

    private ItemStack getLock()
    {
        return auxSlots.getStack(LOCK_SLOT);
    }

    private ItemStack getUpgrading()
    {
        return auxSlots.getStack(UPGRADING_SLOT);
    }

    @Override
    public void tick() {
        if (world.isRemote)
            return;

        ItemHelper.compactInventory(toSort);
        checkFuelOrKlein();
        updateEmc();
        rotateUpgraded();

        if (!openHandlers.isEmpty()) {
            for (EnergyCollectorMK1ScreenHandler handler : openHandlers) {
                handler.sendContentUpdates();
            }
        }
    }

    private void rotateUpgraded()
    {
        if (!StackUtil.isEmpty(getUpgraded()))
        {
            if (StackUtil.isEmpty(getLock())
                        || getUpgraded().getItem() != getLock().getItem()
                        || getUpgraded().count >= getUpgraded().getMaxCount()) {
                auxSlots.setStack(UPGRADE_SLOT, ItemHelper.insertItemStacked(input, getUpgraded().copy()));
            }
        }
    }

    private void checkFuelOrKlein()
    {
        if (!StackUtil.isEmpty(getUpgrading()) && getUpgrading().getItem() instanceof ItemEmc itemEmc)
        {
            if(itemEmc.getStoredEmc(getUpgrading()) != itemEmc.getMaximumEmc(getUpgrading()))
            {
                hasChargeableItem = true;
                hasFuel = false;
            }
            else
            {
                hasChargeableItem = false;
            }
        }
        else if (!StackUtil.isEmpty(getUpgrading()))
        {
            hasFuel = true;
            hasChargeableItem = false;
        } else
        {
            hasFuel = false;
            hasChargeableItem = false;
        }
    }

    private void updateEmc()
    {
        if (!this.hasMaxedEmc())
        {
            unprocessedEMC += emcGen * (getSunLevel() / 320.0f);
            if (unprocessedEMC >= 1) {
                long emcToAdd = (long) unprocessedEMC;
                this.addEMC(emcToAdd);
                unprocessedEMC -= emcToAdd;
            }
        }

        if (this.getStoredEmc() == 0)
        {
            return;
        }
        else if (hasChargeableItem)
        {
            long toSend = this.getStoredEmc() < emcGen ? this.getStoredEmc() : emcGen;
            ItemEmc item = (ItemEmc) getUpgrading().getItem();

            long itemEmc = item.getStoredEmc(getUpgrading());
            long maxItemEmc = item.getMaximumEmc(getUpgrading());

            if ((itemEmc + toSend) > maxItemEmc)
            {
                toSend = maxItemEmc - itemEmc;
            }

            item.addEmc(getUpgrading(), toSend);
            this.removeEMC(toSend);
        }
        else if (hasFuel)
        {
            if (StackUtil.isEmpty(FuelMapper.getFuelUpgrade(getUpgrading())))
            {
                auxSlots.setStack(UPGRADING_SLOT, null);
            }

            ItemStack result = StackUtil.isEmpty(getLock()) ? FuelMapper.getFuelUpgrade(getUpgrading()) : getLock().copy();

            long upgradeCost = EMCHelper.getEmcValue(result) - EMCHelper.getEmcValue(getUpgrading());

            if (upgradeCost >= 0 && this.getStoredEmc() >= upgradeCost)
            {
                ItemStack upgrade = getUpgraded();

                if (StackUtil.isEmpty(getUpgraded()))
                {
                    this.removeEMC(upgradeCost);
                    auxSlots.setStack(UPGRADE_SLOT, result);
                    getUpgrading().count--;

                    if (getUpgrading().count <= 0) {
                        auxSlots.setStack(UPGRADING_SLOT, null);
                    }
                }
                else if (ItemHelper.basicAreStacksEqual(result, upgrade) && upgrade.count < upgrade.getMaxCount())
                {
                    this.removeEMC(upgradeCost);
                    getUpgraded().count++;
                    getUpgrading().count--;

                    if (getUpgrading().count <= 0) {
                        auxSlots.setStack(UPGRADING_SLOT, null);
                    }
                }
            }
        }
        else
        {
            //Only send EMC when we are not upgrading fuel or charging an item
            long toSend = this.getStoredEmc() < emcGen ? this.getStoredEmc() : emcGen;
            this.sendToAllAcceptors(toSend);
            this.sendRelayBonus();
        }
    }

    public long getEmcToNextGoal()
    {
        if (!StackUtil.isEmpty(getLock()))
        {
            return EMCHelper.getEmcValue(getLock()) - EMCHelper.getEmcValue(getUpgrading());
        }
        else
        {
            return EMCHelper.getEmcValue(FuelMapper.getFuelUpgrade(getUpgrading())) - EMCHelper.getEmcValue(getUpgrading());
        }
    }

    public long getItemCharge()
    {
        if (!StackUtil.isEmpty(getUpgrading()) && getUpgrading().getItem() instanceof ItemEmc itemEmc)
        {
            return itemEmc.getStoredEmc(getUpgrading());
        }

        return -1;
    }

    public double getItemChargeProportion()
    {
        long charge = getItemCharge();

        if (StackUtil.isEmpty(getUpgrading()) || charge <= 0 || !(getUpgrading().getItem() instanceof ItemEmc itemEmc))
        {
            return -1;
        }

        long max = itemEmc.getMaximumEmc(getUpgrading());
        if (charge >= max)
        {
            return 1;
        }

        return (double) charge / max;
    }

    public int getSunLevel()
    {
        if (world.dimension.evaporatesWater)
        {
            return 16;
        }
        return world.getLightLevel(x, y + 1, z) + 1;
    }

    public double getFuelProgress()
    {
        if (StackUtil.isEmpty(getUpgrading()) || !FuelMapper.isStackFuel(getUpgrading()))
        {
            return 0;
        }

        long reqEmc;

        if (!StackUtil.isEmpty(getLock()))
        {
            reqEmc = EMCHelper.getEmcValue(getLock()) - EMCHelper.getEmcValue(getUpgrading());

            if (reqEmc < 0)
            {
                return 0;
            }
        }
        else
        {
            if (StackUtil.isEmpty(FuelMapper.getFuelUpgrade(getUpgrading())))
            {
                auxSlots.setStack(UPGRADING_SLOT, null);
                return 0;
            }
            else
            {
                reqEmc = EMCHelper.getEmcValue(FuelMapper.getFuelUpgrade(getUpgrading())) - EMCHelper.getEmcValue(getUpgrading());
            }

        }

        if (getStoredEmc() >= reqEmc)
        {
            return 1;
        }

        return (double) getStoredEmc() / reqEmc;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        storedFuelEmc = tag.getLong("FuelEMC");
        unprocessedEMC = tag.getDouble("UnprocessedEMC");
        if(tag.contains("Input")) {
            InventoryHelper.readNbtList(tag.getList("Input"), input);
        }
        if(tag.contains("AuxSlots")) {
            InventoryHelper.readNbtList(tag.getList("AuxSlots"), auxSlots);
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putLong("FuelEMC", storedFuelEmc);
        tag.putDouble("UnprocessedEMC", unprocessedEMC);
        tag.put("Input", InventoryHelper.toNbtList(input));
        tag.put("AuxSlots", InventoryHelper.toNbtList(auxSlots));
    }

    private void sendRelayBonus()
    {
        for (Map.Entry<Direction, BlockEntity> entry: WorldHelper.getAdjacentBlockEntitiesMapped(world, this).entrySet())
        {
            Direction dir = entry.getKey();
            BlockEntity blockEntity = entry.getValue();

            if (blockEntity instanceof AntiMatterRelayMK1BlockEntity)
            {
                ((AntiMatterRelayMK1BlockEntity) blockEntity).addBonus(dir, 0.5);
            }
            else if (blockEntity instanceof AntiMatterRelayMK2BlockEntity)
            {
                ((AntiMatterRelayMK2BlockEntity) blockEntity).addBonus(dir, 0.15);
            }
            else if (blockEntity instanceof AntiMatterRelayMK3BlockEntity)
            {
                ((AntiMatterRelayMK3BlockEntity) blockEntity).addBonus(dir, 0.05);
            }
        }
    }

    @Override
    public long provideEMC(@NotNull Direction side, long toExtract) {
        long toRemove = Math.min(currentEMC, toExtract);
        removeEMC(toRemove);
        return toRemove;
    }

    @Override
    public long acceptEMC(@NotNull Direction side, long toAccept) {
        if (hasFuel || hasChargeableItem)
        {
            //Collector accepts EMC from providers if it has fuel/chargeable. Otherwise it sends it to providers
            long toAdd = Math.min(maximumEMC - currentEMC, toAccept);
            currentEMC += toAdd;
            return toAdd;
        }
        return 0;
    }

    // ItemHandler
    @Override
    public boolean canInsertItem(@Nullable Direction side) {
        return side != null && !side.getAxis().isVertical();
    }

    @Override
    public boolean canExtractItem(@Nullable Direction side) {
        return side != null && side.getAxis().isVertical();
    }

    @Override
    public boolean canConnectItem(Direction side) {
        return true;
    }

    @Override
    public int getItemSlots(@Nullable Direction side) {
        if (side != null && side.getAxis().isVertical()) {
            return getAux().size();
        }
        return getInput().size();
    }

    @Override
    public ItemStack insertItem(ItemStack stack, int slot, @Nullable Direction side) {
        if (StackUtil.isEmpty(stack)) {
            return null;
        }

        if (side != null && side.getAxis().isVertical()) {
            return stack;
        }

        if (!SlotPredicates.COLLECTOR_INV.test(stack)) {
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

        if (side != null && side.getAxis().isVertical()) {
            return stack;
        }

        ItemStack remainder = stack.copy();
        int slots = getItemSlots(side);

        for (int i = 0; i < slots; i++) {
            remainder = insertItem(remainder, i, side);
            if (StackUtil.isEmpty(remainder)) {
                return null;
            }
        }

        return remainder;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, @Nullable Direction side) {
        if (side == null || !side.getAxis().isVertical()) {
            return null;
        }

        if (slot != EnergyCollectorMK1BlockEntity.UPGRADE_SLOT) {
            return null;
        }

        ItemStack stack = getAux().getStack(slot);

        if (StackUtil.isEmpty(stack)) {
            return null;
        }

        int extractAmount = Math.min(amount, stack.count);
        ItemStack extracted = stack.copy();
        extracted.count = extractAmount;

        stack.count -= extractAmount;
        if (stack.count <= 0) {
            getAux().setStack(slot, null);
        } else {
            getAux().markDirty();
        }

        return extracted;
    }

    @Override
    public ItemStack getItem(int slot, @Nullable Direction side) {
        if (side != null && side.getAxis().isVertical()) {
            if (slot == UPGRADE_SLOT) {
                return getAux().getStack(slot);
            }
            return null;
        }

        if (slot >= 0 && slot < getInput().size()) {
            return getInput().getStack(slot);
        }
        return null;
    }

    @Override
    public boolean setItem(ItemStack stack, int slot, @Nullable Direction side) {
        Inventory target = (side != null && side.getAxis().isVertical()) ? getAux() : getInput();
        if (slot >= 0 && slot < target.size()) {
            target.setStack(slot, stack);
            return true;
        }
        return false;
    }

    @Override
    public ItemStack[] getInventory(@Nullable Direction side) {
        Inventory target = (side != null && side.getAxis().isVertical()) ? getAux() : getInput();
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
