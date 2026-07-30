package net.ralf2oo2.projecte.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.ralf2oo2.projecte.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.modificationstation.stationapi.api.StationAPI;
import net.ralf2oo2.projecte.api.capability.KnowledgeEntityCapability;
import net.ralf2oo2.projecte.event.PlayerKnowledgeChangeEvent;
import net.ralf2oo2.projecte.listener.ItemListener;
import net.ralf2oo2.projecte.packet.KnowledgeSyncS2CPacket;
import net.ralf2oo2.projecte.playerdata.Transmutation;
import net.ralf2oo2.projecte.util.EMCHelper;
import net.ralf2oo2.projecte.util.InventoryHelper;
import net.ralf2oo2.projecte.util.ItemHelper;
import net.ralf2oo2.projecte.util.StackUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DefaultKnowledgeEntityCapability extends KnowledgeEntityCapability {
    @Nullable
    private final PlayerEntity player;
    private final List<ItemStack> knowledge = new ArrayList<>();
    private final Inventory inputLocks = new SimpleInventory("transmutation_locks", 9);
    private long emc = 0;
    private boolean fullKnowledge = false;

    public DefaultKnowledgeEntityCapability(@Nullable PlayerEntity player) {
        this.player = player;
    }

    private void fireChangedEvent()
    {
        if (player != null && !player.world.isRemote)
        {
            StationAPI.EVENT_BUS.post(new PlayerKnowledgeChangeEvent(player));
        }
    }

    @Override
    public boolean hasFullKnowledge()
    {
        return fullKnowledge;
    }

    @Override
    public void setFullKnowledge(boolean fullKnowledge)
    {
        boolean changed = this.fullKnowledge != fullKnowledge;
        this.fullKnowledge = fullKnowledge;
        if (changed)
        {
            fireChangedEvent();
        }
    }

    @Override
    public void clearKnowledge()
    {
        knowledge.clear();
        fullKnowledge = false;
        fireChangedEvent();
    }

    @Override
    public boolean hasKnowledge(@NotNull ItemStack stack) {
        if (StackUtil.isEmpty(stack))
        {
            return false;
        }

        if (fullKnowledge)
        {
            return true;
        }

        for (ItemStack s : knowledge)
        {
            if (ItemHelper.basicAreStacksEqual(s, stack))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addKnowledge(@NotNull ItemStack stack) {
        if (fullKnowledge)
        {
            return false;
        }

        if (stack.getItem() == ItemListener.tome)
        {
            if (!hasKnowledge(stack))
            {
                knowledge.add(stack);
            }
            fullKnowledge = true;
            fireChangedEvent();
            return true;
        }

        if (!hasKnowledge(stack))
        {
            knowledge.add(stack);
            fireChangedEvent();
            return true;
        }

        return false;
    }

    @Override
    public boolean removeKnowledge(@NotNull ItemStack stack) {
        boolean removed = false;

        if (stack.getItem() == ItemListener.tome)
        {
            fullKnowledge = false;
            removed = true;
        }

        if (fullKnowledge)
        {
            return false;
        }

        Iterator<ItemStack> iter = knowledge.iterator();

        while (iter.hasNext())
        {
            if (ItemHelper.basicAreStacksEqual(stack, iter.next()))
            {
                iter.remove();
                removed = true;
            }
        }

        if (removed)
        {
            fireChangedEvent();
        }
        return removed;
    }

    @Override
    public @NotNull List<ItemStack> getKnowledge() {
        return fullKnowledge ? Transmutation.getCachedTomeKnowledge() : Collections.unmodifiableList(knowledge);
    }

    @Override
    public @NotNull Inventory getInputAndLocks() {
        return inputLocks;
    }

    @Override
    public long getEmc() {
        return emc;
    }

    @Override
    public void setEmc(long emc) {
        this.emc = emc;
    }

    // TODO: add packet
    @Override
    public void sync(@NotNull PlayerEntity player)
    {
        System.out.println("Syncing with " + player.name);
        PacketHelper.sendTo(player, new KnowledgeSyncS2CPacket(writeNbt()));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        emc = nbt.getLong("transmutationEmc");

        NbtList list = nbt.getList("knowledge");
        for (int i = 0; i < list.size(); i++)
        {
            ItemStack item = new ItemStack((NbtCompound) list.get(i));
            if (!StackUtil.isEmpty(item))
            {
                knowledge.add(item);
            }
        }

        pruneStaleKnowledge();
        pruneDuplicateKnowledge();

        for (int i = 0; i < inputLocks.size(); i++)
        {
            inputLocks.setStack(i, null);
        }

        for (int i = 0; i < inputLocks.size(); i++) {
            inputLocks.setStack(i, null);
        }

        if (nbt.contains("inputlock")) {
            InventoryHelper.readNbtList(nbt.getList("inputlock"), inputLocks);
        } else {
            for (int i = 0; i < inputLocks.size(); i++) {
                inputLocks.setStack(i, null);
            }
        }

        fullKnowledge = nbt.getBoolean("fullknowledge");
    }

    @Override
    public NbtCompound writeNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putLong("transmutationEmc", emc);

        NbtList knowledgeWrite = new NbtList();
        for (ItemStack i : knowledge)
        {
            NbtCompound tag = i.writeNbt(new NbtCompound());
            knowledgeWrite.add(tag);
        }

        nbt.put("knowledge", knowledgeWrite);
        nbt.put("inputlock", InventoryHelper.toNbtList(inputLocks));

        nbt.putBoolean("fullknowledge", fullKnowledge);
        return nbt;
    }

    private void pruneDuplicateKnowledge()
    {
        ItemHelper.compactItemListNoStacksize(knowledge);
        for (ItemStack s : knowledge)
        {
            if (s.count > 1)
            {
                s.count = 1;
            }
        }
    }

    private void pruneStaleKnowledge()
    {
        knowledge.removeIf(stack -> !EMCHelper.doesItemHaveEmc(stack));
    }
}
