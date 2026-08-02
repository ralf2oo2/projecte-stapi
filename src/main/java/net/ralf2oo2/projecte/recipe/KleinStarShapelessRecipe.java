package net.ralf2oo2.projecte.recipe;

import com.mojang.datafixers.util.Either;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.tag.TagKey;
import net.modificationstation.stationapi.impl.recipe.StationShapelessRecipe;
import net.ralf2oo2.projecte.item.KleinStarItem;
import net.ralf2oo2.projecte.util.StackUtil;

public class KleinStarShapelessRecipe extends StationShapelessRecipe {
    public KleinStarShapelessRecipe(ItemStack output, Either<TagKey<Item>, ItemStack>[] ingredients) {
        super(output, ingredients);
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack result = super.craft(craftingInventory);
        long storedEMC = 0;
        for (int i = 0; i < craftingInventory.size(); i++) {
            ItemStack stack = craftingInventory.getStack(i);
            if (!StackUtil.isEmpty(stack) && stack.getItem() instanceof KleinStarItem) {
                storedEMC += KleinStarItem.getEmc(stack);
            }
        }
        if (storedEMC != 0 && result.getItem() instanceof KleinStarItem) {
            KleinStarItem.setEmc(result, storedEMC);
        }
        return result;
    }

    @Override
    public boolean areItemsEqual(ItemStack stack, ItemStack other) {
        if(stack == null || other == null) {
            return false;
        }
        return stack.getItem() == other.getItem() && stack.getDamage() == other.getDamage();
    }
}
