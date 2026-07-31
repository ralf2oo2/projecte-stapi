package net.ralf2oo2.projecte.block;

import net.danygames2014.nyalib.block.DropInventoryOnBreak;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.block.entity.AlchemicalChestBlockEntity;
import net.ralf2oo2.projecte.screen.handler.AlchemicalChestScreenHandler;

public class AlchemicalChestBlock extends DirectionalBlock implements DropInventoryOnBreak {
    public AlchemicalChestBlock(Identifier identifier) {
        super(identifier, Material.STONE);
        this.setHardness(10.0F);
        this.setResistance(6000000.0F);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new AlchemicalChestBlockEntity();
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if(!world.isRemote && world.getBlockEntity(x, y, z) instanceof AlchemicalChestBlockEntity blockEntity) {
            GuiHelper.openGUI(player, ProjectE.NAMESPACE.id("alchemical_chest"), blockEntity, new AlchemicalChestScreenHandler(player.inventory, blockEntity));
        }
        return true;
    }
}
