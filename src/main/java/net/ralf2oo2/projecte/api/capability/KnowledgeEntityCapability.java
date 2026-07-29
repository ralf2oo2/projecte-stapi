package net.ralf2oo2.projecte.api.capability;

import net.danygames2014.nyalib.capability.entity.EntityCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This interface defines the contract for some object that exposes transmutation knowledge through the Capability system.
 */
public abstract class KnowledgeEntityCapability extends EntityCapability {
    /**
     * @return Whether the player has the "tome" flag set, meaning all knowledge checks automatically return true
     */
    public abstract boolean hasFullKnowledge();

    /**
     * @param fullKnowledge Whether the player has the "tome" flag set, meaning all knowledge checks automatically return true
     */
    public abstract void setFullKnowledge(boolean fullKnowledge);

    /**
     * Clears all knowledge. Additionally, clears the "tome" flag.
     */
    public abstract void clearKnowledge();

    /**
     * @param stack The stack to query
     * @return Whether the player has transmutation knowledge for this stack
     */
    public abstract boolean hasKnowledge(@NotNull ItemStack stack);

    /**
     * @param stack The stack to add to knowledge
     * @return Whether the operation was successful
     */
    public abstract boolean addKnowledge(@NotNull ItemStack stack);

    /**
     * @param stack The stack to remove from knowledge
     * @return Whether the operation was successful
     */
    public abstract boolean removeKnowledge(@NotNull ItemStack stack);

    /**
     * @return An unmodifiable but live view of the knowledge list.
     */
    public abstract @NotNull List<ItemStack> getKnowledge();

    /**
     * @return The player's input and lock slots
     */
    public abstract @NotNull Inventory getInputAndLocks();

    /**
     * @return The emc in this player's transmutation tablet network
     */
    public abstract long getEmc();

    /**
     * @param emc The emc to set in this player's transmutation tablet network
     */
    public abstract void setEmc(long emc);

    /**
     * @param player The player to sync to.
     */
    public abstract void sync(@NotNull PlayerEntity player);

    public abstract NbtCompound writeNbt();

    public abstract void readNbt(NbtCompound nbt);
}
