package net.ralf2oo2.projecte.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.block.entity.*;
import net.ralf2oo2.projecte.screen.handler.*;
import net.ralf2oo2.projecte.util.Constants;

public class AntiMatterRelayBlock extends DirectionalBlock{
    private final int tier;

    public AntiMatterRelayBlock(Identifier identifier, int tier) {
        super(identifier, Material.STONE);
        this.setLuminance(Constants.COLLECTOR_LIGHT_VALS[tier - 1]);
        this.setHardness(10.0F);
        this.tier = tier;
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {

        switch (tier) {
            case 1:
                if(!world.isRemote && world.getBlockEntity(x, y, z) instanceof AntiMatterRelayMK1BlockEntity blockEntity) {
                    GuiHelper.openGUI(player, ProjectE.NAMESPACE.id("anti_matter_relay_mk1"), blockEntity.getInput(), new AntiMatterRelayMK1ScreenHandler(player.inventory, blockEntity), (messagePacket) -> {
                        messagePacket.ints = new int[]{messagePacket.ints != null ? messagePacket.ints[0] : 0, x, y, z};
                    });
                }
                break;
            case 2:
                if(!world.isRemote && world.getBlockEntity(x, y, z) instanceof AntiMatterRelayMK2BlockEntity blockEntity) {
                    GuiHelper.openGUI(player, ProjectE.NAMESPACE.id("anti_matter_relay_mk2"), blockEntity.getInput(), new AntiMatterRelayMK2ScreenHandler(player.inventory, blockEntity), (messagePacket) -> {
                        messagePacket.ints = new int[]{messagePacket.ints != null ? messagePacket.ints[0] : 0, x, y, z};
                    });
                }
                break;
            case 3:
                if(!world.isRemote && world.getBlockEntity(x, y, z) instanceof AntiMatterRelayMK3BlockEntity blockEntity) {
                    GuiHelper.openGUI(player, ProjectE.NAMESPACE.id("anti_matter_relay_mk3"), blockEntity.getInput(), new AntiMatterRelayMK3ScreenHandler(player.inventory, blockEntity), (messagePacket) -> {
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
            case 3 -> new AntiMatterRelayMK1BlockEntity();
            case 2 -> new AntiMatterRelayMK2BlockEntity();
            case 1 -> new AntiMatterRelayMK3BlockEntity();
            default -> null;
        };
    }

    @Override
    public boolean isSolidFace(BlockView blockView, int x, int y, int z, int face) {
        return true;
    }
}
