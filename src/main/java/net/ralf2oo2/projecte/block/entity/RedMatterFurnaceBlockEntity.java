package net.ralf2oo2.projecte.block.entity;

import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.recipe.FuelRegistry;
import net.modificationstation.stationapi.api.recipe.SmeltingRegistry;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.tag.TagKey;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.ralf2oo2.projecte.api.blockentity.EmcAcceptor;
import net.ralf2oo2.projecte.api.item.ItemEmc;
import net.ralf2oo2.projecte.block.MatterFurnaceBlock;
import net.ralf2oo2.projecte.inventory.SimpleInventory;
import net.ralf2oo2.projecte.screen.handler.RedMatterFurnaceScreenHandler;
import net.ralf2oo2.projecte.util.InventoryHelper;
import net.ralf2oo2.projecte.util.ItemHelper;
import net.ralf2oo2.projecte.util.StackUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RedMatterFurnaceBlockEntity extends EmcBlockEntity implements EmcAcceptor {
    private static final long EMC_CONSUMPTION = 2;
    private final Inventory inputInventory = new SimpleInventory("input", getInvSize());
    private final Inventory outputInventory = new SimpleInventory("output", getInvSize());
    private final Inventory fuelInv = new SimpleInventory("fuel", 1);
//    private final IItemHandlerModifiable automationInput = new WrappedItemHandler(inputInventory, WrappedItemHandler.WriteMode.IN)
    protected final int ticksBeforeSmelt;
    private final int efficiencyBonus;
    public int furnaceBurnTime;
    public int currentItemBurnTime;
    public int furnaceCookTime;

    private final List<RedMatterFurnaceScreenHandler> openHandlers = new ArrayList<>();

    public RedMatterFurnaceBlockEntity() {
        this(3, 4);
    }

    protected RedMatterFurnaceBlockEntity(int ticksBeforeSmelt, int efficiencyBonus) {
        super(64);
        this.ticksBeforeSmelt = ticksBeforeSmelt;
        this.efficiencyBonus = efficiencyBonus;
    }

    public void addOpenHandler(RedMatterFurnaceScreenHandler handler) {
        openHandlers.add(handler);
    }

    public void removeOpenHandler(RedMatterFurnaceScreenHandler handler) {
        openHandlers.remove(handler);
    }

    protected int getInvSize()
    {
        return 13;
    }

    protected float getOreDoubleChance() {
        return 1F;
    }

    public Inventory getFuel()
    {
        return fuelInv;
    }

    private ItemStack getFuelItem()
    {
        return fuelInv.getStack(0);
    }

    public Inventory getInput()
    {
        return inputInventory;
    }

    public Inventory getOutput()
    {
        return outputInventory;
    }

    @Override
    public void tick() {
        boolean flag = furnaceBurnTime > 0;
        boolean flag1 = false;

        if (furnaceBurnTime > 0)
        {
            --furnaceBurnTime;
        }

        if (!this.world.isRemote)
        {
            pullFromInventories();
            ItemHelper.compactInventory(inputInventory);

            if (canSmelt() && !StackUtil.isEmpty(getFuelItem()) && getFuelItem().getItem() instanceof ItemEmc itemEmc)
            {
                if (itemEmc.getStoredEmc(getFuelItem()) >= EMC_CONSUMPTION)
                {
                    itemEmc.extractEmc(getFuelItem(), EMC_CONSUMPTION);
                    this.addEMC(EMC_CONSUMPTION);
                }
            }

            if (this.getStoredEmc() >= EMC_CONSUMPTION)
            {
                furnaceBurnTime = 1;
                this.removeEMC(EMC_CONSUMPTION);
            }

            if (furnaceBurnTime == 0 && canSmelt())
            {
                currentItemBurnTime = furnaceBurnTime = getItemBurnTime(getFuelItem());

                if (furnaceBurnTime > 0)
                {
                    flag1 = true;

                    if (!StackUtil.isEmpty(getFuelItem()))
                    {
                        ItemStack copy = getFuelItem().copy();

                        getFuelItem().count--;

                        if (StackUtil.isEmpty(getFuelItem()))
                        {
                            fuelInv.setStack(0, null); // TODO: set to fuel return item if there is an api for that in the future copy.getItem().getContainerItem(copy)
                        }
                    }
                }
            }

            if (furnaceBurnTime > 0 && canSmelt())
            {
                ++furnaceCookTime;

                if (furnaceCookTime == ticksBeforeSmelt)
                {
                    furnaceCookTime = 0;
                    smeltItem();
                    flag1 = true;
                }
            }

            if (flag != furnaceBurnTime > 0)
            {
                flag1 = true;
                Block block = world.getBlockState(x, y, z).getBlock();

                if (!this.world.isRemote && block instanceof MatterFurnaceBlock matterFurnace)
                {
                    matterFurnace.updateFurnaceBlockState(furnaceBurnTime > 0, world, x, y, z);
                }
            }

            if (flag1)
            {
                markDirty();
            }

            ItemHelper.compactInventory(outputInventory);
            pushToInventories();
        }
        if (!openHandlers.isEmpty()) {
            for (RedMatterFurnaceScreenHandler handler : openHandlers) {
                handler.sendContentUpdates();
            }
        }
    }

    public boolean isBurning()
    {
        return furnaceBurnTime > 0;
    }

    private void pullFromInventories()
    {
        BlockEntity blockEntity = this.world.getBlockEntity(x, y + 1, z);
        if (blockEntity == null || blockEntity instanceof DispenserBlockEntity)
            return;
        ItemHandlerBlockCapability handler = CapabilityHelper.getCapability(blockEntity, ItemHandlerBlockCapability.class);

        if (handler == null || !handler.canExtractItem(Direction.DOWN)) {
            return;
        }

        int slots = handler.getItemSlots(Direction.DOWN);

        for (int i = 0; i < slots; i++) {
            ItemStack stackInSlot = handler.getItem(i, Direction.DOWN);
            if (stackInSlot == null || stackInSlot.count <= 0) {
                continue;
            }

            Inventory targetInv = (stackInSlot.getItem() instanceof ItemEmc || !StackUtil.isEmpty(stackInSlot) && FuelRegistry.getFuelTime(stackInSlot) > 0)
                                            ? this.fuelInv : this.inputInventory;



            int fitAmount = ItemHelper.getSpaceForStack(targetInv, stackInSlot);

            if (fitAmount > 0) {
                ItemStack extracted = handler.extractItem(i, fitAmount, Direction.DOWN);

                if (extracted != null && extracted.count > 0) {
                    ItemHelper.insertItemStacked(targetInv, extracted);
                    targetInv.markDirty();
                }
            }
        }
    }

    private void pushToInventories()
    {
        // todo push to others
    }

    private void smeltItem()
    {
        ItemStack toSmelt = inputInventory.getStack(0);
        ItemStack smeltResult = SmeltingRegistry.getResultFor(toSmelt).copy();

        if (world.random.nextFloat() < getOreDoubleChance() && toSmelt.getItem().getRegistryEntry().isIn(TagKey.of(ItemRegistry.KEY, Identifier.of("c:ores")))){
            smeltResult.count += smeltResult.count;
        }

        ItemHelper.insertItemStacked(outputInventory, smeltResult);

        toSmelt.count--;
        if(toSmelt.count <= 0) {
            inputInventory.setStack(0, null);
        }
    }

    private boolean canSmelt()
    {
        ItemStack toSmelt = inputInventory.getStack(0);

        if (StackUtil.isEmpty(toSmelt))
        {
            return false;
        }

        ItemStack smeltResult = SmeltingRegistry.getResultFor(toSmelt);
        if (StackUtil.isEmpty(smeltResult))
        {
            return false;
        }

        ItemStack currentSmelted = outputInventory.getStack(outputInventory.size() - 1);

        if (StackUtil.isEmpty(currentSmelted))
        {
            return true;
        }
        if (!smeltResult.isItemEqual(currentSmelted))
        {
            return false;
        }

        int result = currentSmelted.count + smeltResult.count;
        return result <= currentSmelted.getMaxCount();
    }

    private int getItemBurnTime(ItemStack stack)
    {
        int val = FuelRegistry.getFuelTime(stack);
        return (val * ticksBeforeSmelt) / 200 * efficiencyBonus;
    }

    public int getCookProgressScaled(int value)
    {
        return (furnaceCookTime + (isBurning() && canSmelt() ? 1 : 0)) * value / ticksBeforeSmelt;
    }

    @Environment(EnvType.CLIENT)
    public int getBurnTimeRemainingScaled(int value)
    {
        if (this.currentItemBurnTime == 0)
            this.currentItemBurnTime = ticksBeforeSmelt;

        return furnaceBurnTime * value / currentItemBurnTime;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        furnaceBurnTime = tag.getShort("BurnTime");
        furnaceCookTime = tag.getShort("CookTime");
        if(tag.contains("Input")) {
            InventoryHelper.readNbtList(tag.getList("Input"), inputInventory);
        }
        if(tag.contains("Output")) {
            InventoryHelper.readNbtList(tag.getList("Output"), outputInventory);
        }
        if(tag.contains("Fuel")) {
            InventoryHelper.readNbtList(tag.getList("Fuel"), fuelInv);
        }
        currentItemBurnTime = getItemBurnTime(getFuelItem());
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putShort("BurnTime", (short) furnaceBurnTime);
        tag.putShort("CookTime", (short) furnaceCookTime);
        tag.put("Input", InventoryHelper.toNbtList(inputInventory));
        tag.put("Output", InventoryHelper.toNbtList(outputInventory));
        tag.put("Fuel", InventoryHelper.toNbtList(fuelInv));
    }

    @Override
    public long acceptEMC(@NotNull Direction side, long toAccept) {
        if (this.getStoredEmc() < EMC_CONSUMPTION)
        {
            long needed = EMC_CONSUMPTION - this.getStoredEmc();
            long accept = Math.min(needed, toAccept);
            this.addEMC(accept);
            return accept;
        }
        return 0;
    }
}
