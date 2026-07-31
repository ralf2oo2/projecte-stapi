package net.ralf2oo2.projecte.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.inventory.TransmutationInventory;
import net.ralf2oo2.projecte.screen.handler.TransmutationScreenHandler;

public class TransmutationTableBlock extends TemplateBlock {
    private static final Box BOX = Box.create(0, 0, 0, 1, 0.25, 1);

    public TransmutationTableBlock(Identifier identifier) {
        super(identifier, Material.STONE);
        this.setHardness(10.0F);
        this.setBoundingBox((float) BOX.minX, (float) BOX.minY, (float) BOX.minZ, (float) BOX.maxX, (float) BOX.maxY, (float) BOX.maxZ);
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if(!world.isRemote) {
            GuiHelper.openGUI(player, ProjectE.NAMESPACE.id("transmutation"), player.inventory, new TransmutationScreenHandler(player.inventory, new TransmutationInventory(player)));
        }

        return true;
    }

    @Override
    public Box getBoundingBox(World world, int x, int y, int z) {
        return BOX;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }
}
