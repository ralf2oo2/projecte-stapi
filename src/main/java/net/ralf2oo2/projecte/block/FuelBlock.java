package net.ralf2oo2.projecte.block;

import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;

public class FuelBlock extends TemplateBlock {
    public FuelBlock(Identifier identifier) {
        super(identifier, Material.STONE);
        this.setHardness(0.5F);
    }
}
