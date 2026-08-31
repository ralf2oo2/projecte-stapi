package net.ralf2oo2.projecte.item;

import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class CatalyticLensItem extends DestructionCatalystItem{
    public CatalyticLensItem(Identifier identifier) {
        super(identifier);
    }

    @Override
    public int getNumCharges(@NotNull ItemStack stack) {
        return 7;
    }
}
