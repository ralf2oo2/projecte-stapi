package net.ralf2oo2.projecte.block.entity;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.ralf2oo2.projecte.inventory.CombinedInventoryWrapper;
import net.ralf2oo2.projecte.inventory.SimpleInventory;
import net.ralf2oo2.projecte.inventory.WrappedInventory;
import net.ralf2oo2.projecte.screen.slot.SlotPredicates;
import net.ralf2oo2.projecte.util.EMCHelper;
import net.ralf2oo2.projecte.util.StackUtil;

public class EnergyCondenserMK2BlockEntity extends EnergyCondenserBlockEntity{
    protected Inventory createAutomationInventory()
    {
        Inventory automationInput = new WrappedInventory(getInput(), WrappedInventory.WriteMode.IN)
        {
            @Override
            public void setStack(int slot, ItemStack stack)
            {
                if(SlotPredicates.HAS_EMC.test(stack) && !isStackEqualToLock(stack)) {
                    super.setStack(slot, stack);
                }
            }
        };
        Inventory automationOutput = new WrappedInventory(getOutput(), WrappedInventory.WriteMode.OUT);
        return new CombinedInventoryWrapper(automationInput, automationOutput);
    }

    @Override
    protected Inventory createInput() {
        return new SimpleInventory("input", 42, this::markDirty);
    }

    @Override
    protected Inventory createOutput() {
        return new SimpleInventory("output", 42, this::markDirty);
    }

    @Override
    protected void condense() {
        while (this.hasSpace() && this.getStoredEmc() >= requiredEmc)
        {
            pushStack();
            this.removeEMC(requiredEmc);
        }

        if (this.hasSpace())
        {
            for (int i = 0; i < getInput().size(); i++)
            {
                ItemStack stack = getInput().getStack(i);

                if (StackUtil.isEmpty(stack))
                {
                    continue;
                }

                this.addEMC(EMCHelper.getEmcSellValue(stack) * stack.count);
                getInput().setStack(i, null);
                break;
            }
        }
    }
}
