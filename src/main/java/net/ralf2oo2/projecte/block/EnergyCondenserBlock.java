package net.ralf2oo2.projecte.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.block.entity.EnergyCondenserBlockEntity;
import net.ralf2oo2.projecte.screen.handler.EnergyCondenserScreenHandler;

public class EnergyCondenserBlock extends AlchemicalChestBlock{
    public EnergyCondenserBlock(Identifier identifier) {
        super(identifier);
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if(!world.isRemote && world.getBlockEntity(x, y, z) instanceof EnergyCondenserBlockEntity blockEntity) {
            GuiHelper.openGUI(player, ProjectE.NAMESPACE.id("energy_condenser"), blockEntity.getInput(), new EnergyCondenserScreenHandler(player.inventory, blockEntity), (messagePacket) -> {
                messagePacket.ints = new int[]{messagePacket.ints != null ? messagePacket.ints[0] : 0, x, y, z};
            });
        }

        return true;
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new EnergyCondenserBlockEntity();
    }
}
