package net.ralf2oo2.projecte.block.entity;

import net.minecraft.inventory.Inventory;
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

import java.util.ArrayList;
import java.util.List;

public class AntiMatterRelayMK1BlockEntity extends EmcBlockEntity implements EmcAcceptor, EmcProvider {
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
                if(SlotPredicates.RELAY_INV.test(stack)) {
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
            if (!openHandlers.isEmpty()) {
                for (AntiMatterRelayMK1ScreenHandler handler : openHandlers) {
                    handler.sendContentUpdates();
                }
            }
        }

        ItemStack chargeable = getCharging();

        if (!StackUtil.isEmpty(chargeable) && this.getStoredEmc() > 0 && chargeable.getItem() instanceof ItemEmc)
        {
            chargeItem(chargeable);
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
}
