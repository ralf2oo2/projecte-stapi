package net.ralf2oo2.projecte.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.block.entity.EnergyCondenserBlockEntity;
import net.ralf2oo2.projecte.block.entity.EnergyCondenserMK2BlockEntity;
import net.ralf2oo2.projecte.screen.handler.EnergyCondenserMK2ScreenHandler;
import net.ralf2oo2.projecte.screen.handler.EnergyCondenserScreenHandler;

public class EnergyCondenserMK2Block extends EnergyCondenserBlock{
    public EnergyCondenserMK2Block(Identifier identifier) {
        super(identifier);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new EnergyCondenserMK2BlockEntity();
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if(!world.isRemote && world.getBlockEntity(x, y, z) instanceof EnergyCondenserBlockEntity blockEntity) {
            GuiHelper.openGUI(player, ProjectE.NAMESPACE.id("energy_condenser_mk2"), blockEntity.getInput(), new EnergyCondenserMK2ScreenHandler(player.inventory, blockEntity), (messagePacket) -> {
                messagePacket.ints = new int[]{messagePacket.ints != null ? messagePacket.ints[0] : 0, x, y, z};
            });
        }

        return true;
    }
}
