package net.ralf2oo2.projecte.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.screen.handler.TransmutationScreenHandler;
import net.ralf2oo2.projecte.screen.inventory.TransmutationInventory;

public class TransmutationTabletItem extends ProjectEItem{
    public TransmutationTabletItem(Identifier identifier) {
        super(identifier);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, PlayerEntity user) {
        if(!world.isRemote) {
            GuiHelper.openGUI(user, ProjectE.NAMESPACE.id("transmutation"), user.inventory, new TransmutationScreenHandler(user.inventory, new TransmutationInventory(user)));
        }
        return stack;
    }
}
