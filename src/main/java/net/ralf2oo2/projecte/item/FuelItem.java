package net.ralf2oo2.projecte.item;

import net.modificationstation.stationapi.api.recipe.FuelRegistry;
import net.modificationstation.stationapi.api.util.Identifier;

public class FuelItem extends ProjectEItem{
    public FuelItem(Identifier identifier, int fuelTime) {
        super(identifier);
        FuelRegistry.addFuelItem(this, fuelTime);
    }
}
