package net.ralf2oo2.projecte.block.entity;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.impl.item.StationNBTSetter;
import net.ralf2oo2.projecte.api.blockentity.EmcAcceptor;
import net.ralf2oo2.projecte.inventory.SimpleInventory;
import net.ralf2oo2.projecte.inventory.WrappedInventory;
import net.ralf2oo2.projecte.screen.handler.EnergyCondenserScreenHandler;
import net.ralf2oo2.projecte.screen.slot.SlotPredicates;
import net.ralf2oo2.projecte.util.EMCHelper;
import net.ralf2oo2.projecte.util.ItemHelper;
import net.ralf2oo2.projecte.util.NBTWhitelist;
import net.ralf2oo2.projecte.util.StackUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnergyCondenserBlockEntity extends EmcBlockEntity implements EmcAcceptor {
    protected final Inventory inputInventory = createInput();
    private final Inventory outputInventory = createOutput();
    private final Inventory automationInventory = createAutomationInventory();
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

    protected Inventory createAutomationInventory()
    {
        return new WrappedInventory(inputInventory, WrappedInventory.WriteMode.IN_OUT) {
            @Override
            public void setStack(int slot, ItemStack stack) {
                if (stack != null && SlotPredicates.HAS_EMC.test(stack) && !isStackEqualToLock(stack)) {
                    super.setStack(slot, stack);
                }
            }

            @Override
            public ItemStack removeStack(int slot, int amount) {
                ItemStack stack = getStack(slot);
                if (stack != null && isStackEqualToLock(stack)) {
                    return super.removeStack(slot, amount);
                }
                return null;
            }
        };
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
    public void markDirty() {
        this.updateState();
        super.markDirty();
    }
}
