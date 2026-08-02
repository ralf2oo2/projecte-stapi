package net.ralf2oo2.projecte.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipeManager;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.tag.TagKey;
import net.modificationstation.stationapi.impl.recipe.StationShapelessRecipe;
import net.ralf2oo2.projecte.recipe.KleinStarShapelessRecipe;

import java.util.Optional;

public class CraftingRegistryHelper {
    public static void addKleinStarShapelessRecipe(ItemStack output, Object... ingredients) {
        Either<TagKey<Item>, ItemStack>[] checkedIngredients = new Either[ingredients.length];
        for (int i = 0, ingredientsLength = ingredients.length; i < ingredientsLength; i++) {
            Object ingredient = ingredients[i];
            if (ingredient instanceof ItemStack stack) checkedIngredients[i] = Either.right(stack.copy());
            else if (ingredient instanceof Item item) checkedIngredients[i] = Either.right(new ItemStack(item));
            else if (ingredient instanceof Block block) checkedIngredients[i] = Either.right(new ItemStack(block));
            else if (ingredient instanceof TagKey<?> tag) {
                Optional<TagKey<Item>> itemTagOpt = tag.tryCast(ItemRegistry.KEY);
                if (itemTagOpt.isPresent())
                    checkedIngredients[i] = Either.left(itemTagOpt.get());
            } else throw new RuntimeException("Invalid shapeless recipe ingredient of type " + ingredient.getClass().getName() + "!");
        }
        CraftingRecipeManager.getInstance().getRecipes().add(new KleinStarShapelessRecipe(output, checkedIngredients));
    }
}
