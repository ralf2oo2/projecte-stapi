package net.ralf2oo2.projecte.listener;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.event.gui.screen.container.TooltipBuildEvent;
import net.ralf2oo2.projecte.util.EMCHelper;

public class TooltipListener {
    @EventListener
    public void onTooltipBuild(TooltipBuildEvent event) {
        ItemStack stack = event.itemStack;
        if(stack == null) {
            return;
        }

        if(EMCHelper.doesItemHaveEmc(stack)) {
            event.tooltip.add("EMC: " + EMCHelper.getEmcValue(stack));
            if(stack.count > 1) {
                event.tooltip.add("Stack EMC: " + EMCHelper.getStackEmc(stack));
            }
        }
    }
}
