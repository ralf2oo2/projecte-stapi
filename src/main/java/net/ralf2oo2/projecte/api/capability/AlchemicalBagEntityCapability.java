package net.ralf2oo2.projecte.api.capability;

import net.danygames2014.nyalib.capability.entity.EntityCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.vanillafix.util.DyeColor;
import org.jetbrains.annotations.NotNull;

/**
 * This interface defines the contract for some object that exposes sixteen colored inventories,
 * for the purpose of usage as Alchemical Bags.
 * This is exposed through the Capability system.
 */
public abstract class AlchemicalBagEntityCapability extends EntityCapability {
    /**
     * Note: modifying this clientside is not advised
     * @param color The bag color to acquire
     * @return The inventory representing this alchemical bag
     */
    protected abstract @NotNull Inventory getBag(@NotNull DyeColor color);

    /**
     * Syncs the bag inventory associated with this color to the player provided (usually the owner of this capability instance)
     * @param color The bag color to sync. If null, syncs every color.
     * @param player The player to sync the bags to.
     */
    public abstract void sync(DyeColor color, @NotNull PlayerEntity player);

    public abstract NbtCompound writeNbt();

    public abstract void readNbt(NbtCompound nbt);
}
