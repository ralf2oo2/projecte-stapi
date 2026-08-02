package net.ralf2oo2.projecte.block;

import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;

public class MatterBlock extends TemplateBlock {
    private final boolean redMatter;
    public MatterBlock(Identifier identifier, boolean redMatter) {
        super(identifier, Material.METAL);
        this.setHardness(1000000F);
        this.redMatter = redMatter;
    }

    @Override
    public float getHardness() {
        if (!redMatter)
        {
            return 1000000.0F;
        }
        else
        {
            return 2000000.0F;
        }
    }
}
