package net.ralf2oo2.projecte.item;

import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.api.item.ItemEmc;
import net.ralf2oo2.projecte.api.item.ItemWithDisplayDurability;
import net.ralf2oo2.projecte.util.EMCHelper;
import org.jetbrains.annotations.NotNull;

public class KleinStarItem extends ProjectEItem implements ItemEmc, ItemWithDisplayDurability {
    public Tier tier;

    public KleinStarItem(Identifier identifier, Tier tier) {
        super(identifier);
        this.setMaxCount(1);
        this.setMaxDamage(0);

        this.tier = tier;
    }

    public enum Tier
    {
        EIN("ein"),
        ZWEI("zwei"),
        DREI("drei"),
        VIER("vier"),
        SPHERE("sphere"),
        OMEGA("omega");

        public final String name;
        Tier(String name)
        {
            this.name = name;
        }
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        long starEmc = getEmc(stack);

        if (starEmc == 0)
        {
            return 1.0D;
        }

        return 1.0D - starEmc / (double) EMCHelper.getKleinStarMaxEmc(stack);
    }

    @Override
    public long addEmc(@NotNull ItemStack stack, long toAdd)
    {
        long add = Math.min(getMaximumEmc(stack) - getStoredEmc(stack), toAdd);
        ProjectEItem.addEmcToStack(stack, add);
        return add;
    }

    @Override
    public long extractEmc(@NotNull ItemStack stack, long toRemove)
    {
        long sub = Math.min(getStoredEmc(stack), toRemove);
        ProjectEItem.removeEmc(stack, sub);
        return sub;
    }

    @Override
    public long getStoredEmc(@NotNull ItemStack stack)
    {
        return ProjectEItem.getEmc(stack);
    }

    @Override
    public long getMaximumEmc(@NotNull ItemStack stack)
    {
        return EMCHelper.getKleinStarMaxEmc(stack);
    }
}
