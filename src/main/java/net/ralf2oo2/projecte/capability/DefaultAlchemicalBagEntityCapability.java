package net.ralf2oo2.projecte.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.modificationstation.stationapi.api.vanillafix.util.DyeColor;
import net.ralf2oo2.projecte.api.capability.AlchemicalBagEntityCapability;
import net.ralf2oo2.projecte.inventory.SimpleInventory;
import net.ralf2oo2.projecte.util.InventoryHelper;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class DefaultAlchemicalBagEntityCapability extends AlchemicalBagEntityCapability {
    private final Map<DyeColor, Inventory> inventories = new EnumMap<>(DyeColor.class);

    @Override
    public @NotNull Inventory getBag(@NotNull DyeColor color) {
        if (!inventories.containsKey(color))
        {
            inventories.put(color, new SimpleInventory("Alchemical Bag", 104));
        }

        return inventories.get(color);
    }

    // TODO syncing
    @Override
    public void sync(DyeColor color, @NotNull PlayerEntity player) {
//        PacketHelper.sendTo(new SyncBagDataPKT(writeNBT(color)), player);
    }

    @Override
    public NbtCompound writeNbt() {
        return writeNbt(null);
    }

    public NbtCompound writeNbt(DyeColor color) {
        NbtCompound ret = new NbtCompound();
        DyeColor[] colors = (color == null) ? DyeColor.values() : new DyeColor[] { color };

        for (DyeColor c : colors) {
            Inventory inv = inventories.get(c);
            if (inv != null) {
                ret.put(c.name(), InventoryHelper.toNbtList(inv));
            }
        }
        return ret;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        inventories.clear();
        for (DyeColor e : DyeColor.values()) {
            if (nbt.contains(e.name())) {
                NbtList tagList = nbt.getList(e.name());
                SimpleInventory inv = InventoryHelper.createFromNbtList(tagList, "Alchemical Bag", 104);
                inventories.put(e, inv);
            }
        }
    }
}
