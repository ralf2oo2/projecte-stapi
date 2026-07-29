package net.ralf2oo2.projecte.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.ralf2oo2.projecte.util.EMCHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(Item.class)
public class ItemMixin implements CustomTooltipProvider {

    @Override
    public @NotNull String[] getTooltip(ItemStack stack, String originalTooltip) {
        List<String> tooltip = new ArrayList<>();

        tooltip.add(originalTooltip);

        if(EMCHelper.doesItemHaveEmc(stack)) {
            tooltip.add("EMC: " + EMCHelper.getEmcValue(stack.getItem()));
            tooltip.add("Stack EMC: " + EMCHelper.getStackEmc(stack));
        }

        return tooltip.toArray(new String[0]);
    }
}
