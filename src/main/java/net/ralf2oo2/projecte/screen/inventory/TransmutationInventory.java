package net.ralf2oo2.projecte.screen.inventory;

import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.impl.item.StationNBTSetter;
import net.ralf2oo2.projecte.api.capability.KnowledgeEntityCapability;
import net.ralf2oo2.projecte.api.item.ItemEmc;
import net.ralf2oo2.projecte.emc.FuelMapper;
import net.ralf2oo2.projecte.event.PlayerAttemptLearnEvent;
import net.ralf2oo2.projecte.util.*;

import java.util.*;

public class TransmutationInventory extends CombinedInventoryWrapper{
    public final PlayerEntity player;
    public final KnowledgeEntityCapability capability;
    private final Inventory inputLocks;
    private final Inventory learning;
    public final Inventory outputs;

    private static final int LOCK_INDEX = 8;
    private static final int FUEL_START = 12;
    public int learnFlag = 0;
    public int unlearnFlag = 0;
    public String filter = "";
    public int searchpage = 0;
    public final List<ItemStack> knowledge = new ArrayList<>();

    public TransmutationInventory(PlayerEntity player) {
        super(CapabilityHelper.getCapability(player, KnowledgeEntityCapability.class).getInputAndLocks(), new SimpleInventory("inv", 2), new SimpleInventory("inv", 16));
        this.capability = CapabilityHelper.getCapability(player, KnowledgeEntityCapability.class);

        this.player = player;

        this.inputLocks = inventories[0];
        this.learning = inventories[1];
        this.outputs = inventories[2];

        if(!player.world.isRemote) {
            updateClientTargets();
        }
    }

    public void handleKnowledge(ItemStack stack)
    {
        if (stack.count > 1)
        {
            stack.count = 1;
        }

        if (ItemHelper.isDamageable(stack))
        {
            stack.setDamage(0);
        }

        if (!capability.hasKnowledge(stack))
        {
            if (!NBTWhitelist.shouldDupeWithNBT(stack))
            {
                StationNBTSetter.cast(stack).setStationNbt(new NbtCompound());
            }

            if (!StationAPI.EVENT_BUS.post(new PlayerAttemptLearnEvent(player, stack)).isCanceled()) //Only show the "learned" text if the knowledge was added
            {
                learnFlag = 300;
                unlearnFlag = 0;
                capability.addKnowledge(stack);
            }

            if (!player.world.isRemote)
            {
                capability.sync(player);
            }
        }

        updateClientTargets();
    }

    public void handleUnlearn(ItemStack stack)
    {
        if (stack.count > 1)
        {
            stack.count = 1;
        }

        if (ItemHelper.isDamageable(stack))
        {
            stack.setDamage(0);
        }

        if (capability.hasKnowledge(stack))
        {
            unlearnFlag = 300;
            learnFlag = 0;

            if (!NBTWhitelist.shouldDupeWithNBT(stack))
            {
                StationNBTSetter.cast(stack).setStationNbt(new NbtCompound());
            }

            capability.removeKnowledge(stack);

            if (!player.world.isRemote)
            {
                capability.sync(player);
            }
        }

        updateClientTargets();
    }

    public void checkForUpdates()
    {
        long matterEmc = EMCHelper.getEmcValue(outputs.getStack(0));
        long fuelEmc = EMCHelper.getEmcValue(outputs.getStack(FUEL_START));

        if (Math.max(matterEmc, fuelEmc) > getAvailableEMC())
        {
            updateClientTargets();
        }
    }

    public void updateClientTargets()
    {
        if (this.player.world.isRemote)
        {
            return;
        }

        knowledge.clear();
        knowledge.addAll(capability.getKnowledge());

        for (int i = 0; i < outputs.size(); i++)
        {
            outputs.setStack(i, null);
        }

        ItemStack lockCopy = null;

        knowledge.sort(Collections.reverseOrder(Comparator.comparing(EMCHelper::getEmcValue)));
        if (!StackUtil.isEmpty(inputLocks.getStack(LOCK_INDEX)))
        {
            lockCopy = ItemHelper.getNormalizedStack(inputLocks.getStack(LOCK_INDEX));

            if (ItemHelper.isDamageable(lockCopy))
            {
                lockCopy.setDamage(0);
            }

            long reqEmc = EMCHelper.getEmcValue(inputLocks.getStack(LOCK_INDEX));

            if (getAvailableEMC() < reqEmc)
            {
                return;
            }

            if (!NBTWhitelist.shouldDupeWithNBT(lockCopy))
            {
                StationNBTSetter.cast(lockCopy).setStationNbt(new NbtCompound());
            }

            Iterator<ItemStack> iter = knowledge.iterator();
            int pagecounter = 0;

            while (iter.hasNext())
            {
                ItemStack stack = iter.next();

                if (EMCHelper.getEmcValue(stack) > reqEmc)
                {
                    iter.remove();
                    continue;
                }

                if (ItemHelper.basicAreStacksEqual(lockCopy, stack))
                {
                    iter.remove();
                    continue;
                }

                if (!doesItemMatchFilter(stack)) {
                    iter.remove();
                    continue;
                }

                if (pagecounter < (searchpage * 12))
                {
                    pagecounter++;
                    iter.remove();
                }
            }
        }
        else
        {
            Iterator<ItemStack> iter = knowledge.iterator();
            int pagecounter = 0;

            while (iter.hasNext())
            {
                ItemStack stack = iter.next();

                if (getAvailableEMC() < EMCHelper.getEmcValue(stack))
                {
                    iter.remove();
                    continue;
                }

                if (!doesItemMatchFilter(stack)) {
                    iter.remove();
                    continue;
                }

                if (pagecounter < (searchpage * 12))
                {
                    pagecounter++;
                    iter.remove();
                }
            }
        }

        int matterCounter = 0;
        int fuelCounter = 0;

        if (!StackUtil.isEmpty(lockCopy) && capability.hasKnowledge(lockCopy))
        {
            if (FuelMapper.isStackFuel(lockCopy))
            {
                outputs.setStack(FUEL_START, lockCopy);
                fuelCounter++;
            }
            else
            {
                outputs.setStack(0, lockCopy);
                matterCounter++;
            }
        }

        for (ItemStack stack : knowledge)
        {
            if (FuelMapper.isStackFuel(stack))
            {
                if (fuelCounter < 4)
                {
                    outputs.setStack(FUEL_START + fuelCounter, stack);

                    fuelCounter++;
                }
            }
            else
            {
                if (matterCounter < 12)
                {
                    outputs.setStack(matterCounter, stack);

                    matterCounter++;
                }
            }
        }
    }

    private boolean doesItemMatchFilter(ItemStack stack)
    {
        String displayName;

        try
        {
            displayName = I18n.getTranslation(stack.getTranslationKey()).toLowerCase(Locale.ROOT);
        } catch (Exception e)
        {
            e.printStackTrace();
            //From old code... Not sure if intended to not remove items that crash on getDisplayName
            return true;
        }

        if (displayName == null)
        {
            return false;
        }
        else if (filter.length() > 0 && !displayName.contains(filter))
        {
            return false;
        }
        return true;
    }

    public void writeIntoOutputSlot(int slot, ItemStack item)
    {

        if (EMCHelper.doesItemHaveEmc(item)
                    && EMCHelper.getEmcValue(item) <= getAvailableEMC()
                    && capability.hasKnowledge(item))
        {
            outputs.setStack(slot, item);
        }
        else
        {
            outputs.setStack(slot, null);
        }
    }

    public void addEmc(long value)
    {
        if (value == 0)
        {
            //Optimization to not look at the items if nothing will happen anyways
            return;
        }
        if (value < 0)
        {
            //Make sure it is using the correct method so that it handles the klein stars properly
            removeEmc(-value);
        }
        //Start by trying to add it to the EMC items on the left
        for (int i = 0; i < inputLocks.size(); i++)
        {
            if (i == LOCK_INDEX)
            {
                continue;
            }
            ItemStack stack = inputLocks.getStack(i);
            if (!StackUtil.isEmpty(stack) && stack.getItem() instanceof ItemEmc itemEmc)
            {
                long neededEmc = itemEmc.getMaximumEmc(stack) - itemEmc.getStoredEmc(stack);
                if (value <= neededEmc)
                {
                    //This item can store all of the amount being added
                    itemEmc.addEmc(stack, value);
                    return;
                }
                //else more than this item can fit, so fill the item and then continue going
                itemEmc.addEmc(stack, neededEmc);
                value -= neededEmc;
            }
        }
        long emcToMax = Constants.TILE_MAX_EMC - capability.getEmc();
        if (value > emcToMax)
        {
            long excessEMC = value - emcToMax;
            value = emcToMax;
            //Will finish filling provider
            //Now with excess EMC we can check against the lock slot as that is the last spot that has its EMC used.
            ItemStack stack = inputLocks.getStack(LOCK_INDEX);
            if (!StackUtil.isEmpty(stack) && stack.getItem() instanceof ItemEmc itemEmc)
            {
                long neededEmc = itemEmc.getMaximumEmc(stack) - itemEmc.getStoredEmc(stack);
                if (excessEMC > neededEmc)
                {
                    itemEmc.addEmc(stack, neededEmc);
                }
                else
                {
                    itemEmc.addEmc(stack, excessEMC);
                }
            }
        }

        capability.setEmc(capability.getEmc() + value);

        if (capability.getEmc() >= Constants.TILE_MAX_EMC || capability.getEmc() < 0)
        {
            capability.setEmc(Constants.TILE_MAX_EMC);
        }

        // TODO: check if there is an alternative for this in beta
        if (!player.world.isRemote)
        {
//            PlayerHelper.updateScore((EntityPlayerMP) player, PlayerHelper.SCOREBOARD_EMC, MathHelper.floor(capability.getEmc()));
        }
    }

    public void removeEmc(long value)
    {
        if (value == 0)
        {
            //Optimization to not look at the items if nothing will happen anyways
            return;
        }
        if (value < 0)
        {
            //Make sure it is using the correct method so that it handles the klein stars properly
            addEmc(-value);
        }
        if (hasMaxedEmc())
        {
            //If the EMC is maxed, check and try to remove from the lock slot if it is IItemEMC
            //This is the only case if the provider is full when the IItemEMC was put in the lock slot
            ItemStack stack = inputLocks.getStack(LOCK_INDEX);
            if (!StackUtil.isEmpty(stack) && stack.getItem() instanceof ItemEmc itemEmc)
            {
                long storedEmc = itemEmc.getStoredEmc(stack);
                if (storedEmc >= value)
                {
                    //All of it can be removed from the lock item
                    itemEmc.extractEmc(stack, value);
                    return;
                }
                itemEmc.extractEmc(stack, storedEmc);
                value -= storedEmc;
            }
        }
        if (value > capability.getEmc())
        {
            //Remove from provider first
            //This code runs first to simplify the logic
            //But it simulates removal first by extracting the amount from value and then removing that excess from items
            long toRemove = value - capability.getEmc();
            value = capability.getEmc();
            for (int i = 0; i < inputLocks.size(); i++)
            {
                if (i == LOCK_INDEX)
                {
                    continue;
                }
                ItemStack stack = inputLocks.getStack(i);
                if (!StackUtil.isEmpty(stack) && stack.getItem() instanceof ItemEmc itemEmc)
                {
                    long storedEmc = itemEmc.getStoredEmc(stack);
                    if (toRemove <= storedEmc)
                    {
                        //The EMC that is being removed that the provider does not contain is satisfied by this IItemEMC
                        //Remove it and then
                        itemEmc.extractEmc(stack, toRemove);
                        break;
                    }
                    //Removes all the emc from this item
                    itemEmc.extractEmc(stack, storedEmc);
                    toRemove -= storedEmc;
                }
            }
        }
        capability.setEmc(capability.getEmc() - value);

        if (capability.getEmc() < 0)
        {
            capability.setEmc(0);
        }

        // TODO: check if there is an alternative for this in beta
        if (!player.world.isRemote)
        {
//            PlayerHelper.updateScore((EntityPlayerMP) player, PlayerHelper.SCOREBOARD_EMC, MathHelper.floor(capability.getEmc()));
        }
    }

    public boolean hasMaxedEmc()
    {
        return capability.getEmc() >= Constants.TILE_MAX_EMC;
    }

    public Inventory getInventoryForSlot(int slot)
    {
        if (slot < 0) return null;

        for (Inventory inv : inventories)
        {
            if (slot < inv.size())
            {
                return inv;
            }
            slot -= inv.size();
        }
        return null;
    }

    public int getIndexFromSlot(int slot)
    {
        for (Inventory h : inventories)
        {
            if (slot >= h.size())
            {
                slot -= h.size();
            }
        }

        return slot;
    }

    /**
     * @return EMC available from the Provider + any klein stars in the input slots.
     */
    public long getAvailableEMC()
    {
        //TODO: Cache this value somehow, or at least cache which slots have IItemEMC in them?
        if (hasMaxedEmc())
        {
            return Constants.TILE_MAX_EMC;
        }

        long emc = capability.getEmc();
        long emcToMax = Constants.TILE_MAX_EMC - emc;
        for (int i = 0; i < inputLocks.size(); i++)
        {
            if (i == LOCK_INDEX)
            {
                //Skip it even though this technically could add to available EMC.
                //This is because this case can only happen if the provider is already at max EMC
                continue;
            }
            ItemStack stack = inputLocks.getStack(i);
            if (!StackUtil.isEmpty(stack) && stack.getItem() instanceof ItemEmc itemEmc)
            {
                long storedEmc = itemEmc.getStoredEmc(stack);
                if (storedEmc >= emcToMax)
                {
                    return Constants.TILE_MAX_EMC;
                }
                emc += storedEmc;
                emcToMax -= storedEmc;
            }
        }
        return emc;
    }
}
