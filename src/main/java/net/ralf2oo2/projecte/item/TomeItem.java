package net.ralf2oo2.projecte.item;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TomeItem extends ProjectEItem implements CustomTooltipProvider {
    public TomeItem(Identifier identifier) {
        super(identifier);
        setMaxCount(1);
    }


    @Override
    public @NotNull String[] getTooltip(ItemStack stack, String originalTooltip) {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(originalTooltip);

        tooltip.add(I18n.getTranslation("pe.tome.tooltip1"));

        return tooltip.toArray(new String[0]);
    }
}
