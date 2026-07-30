package net.ralf2oo2.projecte.item;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.vanillafix.util.DyeColor;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.api.capability.AlchemicalBagEntityCapability;
import net.ralf2oo2.projecte.screen.handler.AlchemicalBagScreenHandler;
import net.ralf2oo2.projecte.util.StackUtil;

public class AlchemicalBagItem extends ProjectEItem{
    public final DyeColor color;

    private static final Object2ObjectOpenHashMap<DyeColor, AlchemicalBagItem> allBags = new Object2ObjectOpenHashMap<>();

    public AlchemicalBagItem(Identifier identifier, DyeColor color) {
        super(identifier);
        setMaxCount(1);
        this.color = color;
        allBags.put(color, this);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, PlayerEntity user) {
        if(!world.isRemote) {
            AlchemicalBagEntityCapability capability = CapabilityHelper.getCapability(user, AlchemicalBagEntityCapability.class);

            if(capability != null) {
                GuiHelper.openGUI(user, ProjectE.NAMESPACE.id("alchemical_bag"), capability.getBag(color), new AlchemicalBagScreenHandler(user.inventory, capability.getBag(color)));
            }
        }

        return stack;
    }

    public static boolean isBag(ItemStack stack) {
        if(StackUtil.isEmpty(stack)) {
            return false;
        }
        return allBags.containsKey(stack.getItem());
    }
}
