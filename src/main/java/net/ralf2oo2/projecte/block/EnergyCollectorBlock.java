package net.ralf2oo2.projecte.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.block.entity.EnergyCollectorMK1BlockEntity;
import net.ralf2oo2.projecte.block.entity.EnergyCollectorMK2BlockEntity;
import net.ralf2oo2.projecte.block.entity.EnergyCollectorMK3BlockEntity;
import net.ralf2oo2.projecte.screen.handler.EnergyCollectorMK1ScreenHandler;
import net.ralf2oo2.projecte.screen.handler.EnergyCollectorMK2ScreenHandler;
import net.ralf2oo2.projecte.screen.handler.EnergyCollectorMK3ScreenHandler;
import net.ralf2oo2.projecte.util.Constants;

public class EnergyCollectorBlock extends DirectionalBlock{
    private final int tier;

    public EnergyCollectorBlock(Identifier identifier, int tier) {
        super(identifier, Material.GLASS);
        this.setLuminance(Constants.COLLECTOR_LIGHT_VALS[tier - 1]);
        this.setHardness(0.3f);
        this.tier = tier;
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {

        switch (tier) {
            case 1:
                if(!world.isRemote && world.getBlockEntity(x, y, z) instanceof EnergyCollectorMK1BlockEntity blockEntity) {
                    GuiHelper.openGUI(player, ProjectE.NAMESPACE.id("energy_collector_mk1"), blockEntity.getInput(), new EnergyCollectorMK1ScreenHandler(player.inventory, blockEntity), (messagePacket) -> {
                        messagePacket.ints = new int[]{messagePacket.ints != null ? messagePacket.ints[0] : 0, x, y, z};
                    });
                }
                break;
            case 2:
                if(!world.isRemote && world.getBlockEntity(x, y, z) instanceof EnergyCollectorMK2BlockEntity blockEntity) {
                    GuiHelper.openGUI(player, ProjectE.NAMESPACE.id("energy_collector_mk2"), blockEntity.getInput(), new EnergyCollectorMK2ScreenHandler(player.inventory, blockEntity), (messagePacket) -> {
                        messagePacket.ints = new int[]{messagePacket.ints != null ? messagePacket.ints[0] : 0, x, y, z};
                    });
                }
                break;
            case 3:
                if(!world.isRemote && world.getBlockEntity(x, y, z) instanceof EnergyCollectorMK3BlockEntity blockEntity) {
                    GuiHelper.openGUI(player, ProjectE.NAMESPACE.id("energy_collector_mk3"), blockEntity.getInput(), new EnergyCollectorMK3ScreenHandler(player.inventory, blockEntity), (messagePacket) -> {
                        messagePacket.ints = new int[]{messagePacket.ints != null ? messagePacket.ints[0] : 0, x, y, z};
                    });
                }
                break;
        }

        return true;
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return switch (tier) {
            case 3 -> new EnergyCollectorMK3BlockEntity();
            case 2 -> new EnergyCollectorMK2BlockEntity();
            case 1 -> new EnergyCollectorMK1BlockEntity();
            default -> null;
        };
    }

    @Override
    public boolean isSolidFace(BlockView blockView, int x, int y, int z, int face) {
        return true;
    }
}
