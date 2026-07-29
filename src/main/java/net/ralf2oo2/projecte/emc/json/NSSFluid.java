package net.ralf2oo2.projecte.emc.json;

import net.danygames2014.nyalib.fluid.Fluid;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class NSSFluid implements NormalizedSimpleStack {
    public final Identifier identifier;

    private NSSFluid(Fluid f) {
        this.identifier = f.getIdentifier();
    }

    @NotNull
    public static NormalizedSimpleStack create(Fluid fluid) {
        //TODO cache The fluid normalizedSimpleStacks?
        return new NSSFluid(fluid);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NSSFluid && identifier.equals(((NSSFluid) o).identifier);
    }

    @Override
    public String json() {
        return "FLUID|" + this.identifier;
    }

    @Override
    public int hashCode() {
        return this.identifier.hashCode();
    }

    @Override
    public String toString() {
        return "Fluid: " + this.identifier;
    }
}
