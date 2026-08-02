package net.ralf2oo2.projecte.recipe;

import net.minecraft.block.Block;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.modificationstation.stationapi.api.recipe.SmeltingRegistry;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.tag.TagKey;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.item.PhilosophersStoneItem;
import net.ralf2oo2.projecte.util.StackUtil;

import java.util.*;

public class PhilosopherStoneSmeltingRecipe implements CraftingRecipe {
    @Override
    public boolean matches(CraftingInventory craftingInventory) {
        return !getMatchingRecipes(craftingInventory).isEmpty();
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        Set<ItemStack> matchingRecipes = getMatchingRecipes(craftingInventory);
        if(matchingRecipes.isEmpty()) {
            return null;
        }

        ItemStack output = matchingRecipes.stream().findFirst().get().copy();
        output.count = output.count * 7;
        return output;
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(Block.STONE);
    }

    private Set<ItemStack> getMatchingRecipes(CraftingInventory inv) {
        List<ItemStack> philoStones = new ArrayList<>();
        List<ItemStack> coals = new ArrayList<>();
        List<ItemStack> allItems = new ArrayList<>();
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getStack(i);
            if (!StackUtil.isEmpty(stack)) {
                Item item = stack.getItem();
                allItems.add(stack);
                if (allItems.size() > 9) {
                    //Exit if we have more than 9 items total (for mods that may add larger crafting tables)
                    return Collections.emptySet();
                }
                if (item instanceof PhilosophersStoneItem) {
                    philoStones.add(stack);
                }
                if (item.getRegistryEntry().isIn(TagKey.of(ItemRegistry.KEY, Identifier.of("c:coals")))) {
                    coals.add(stack);
                }
            }
        }
        if (allItems.size() == 9) {
            //If we have exactly 9 items check for a matching recipe
            for (ItemStack philoStone : philoStones) {
                for (ItemStack coal : coals) {
                    //Skip if the philosopher's stone is the same stack as the coal stack
                    // This may be the case if a pack dev added the philosopher's stone to the coals tag
                    if (philoStone != coal) {
                        Set<ItemStack> matchingRecipes = new HashSet<>();
                        for (ItemStack stack : allItems) {
                            //Ignore checking the piece of coal and the philosopher's stone
                            if (stack != philoStone && stack != coal) {
                                //And check all the other elements to find any matching recipes
                                ItemStack result = SmeltingRegistry.getResultFor(stack);
                                if (StackUtil.isEmpty(result) || !matchingRecipes.add(result)) {
                                    //If there are no matching recipes yet see if there are any recipes that match the current stack and add them if they are,
                                    // if we didn't end up adding any elements that means there are no matching recipes so fail
                                    if (StackUtil.isEmpty(result)) {
                                        return Collections.emptySet();
                                    }
                                } else if (matchingRecipes.removeIf(recipe -> StackUtil.isEmpty(result) || recipe.getItem() != result.getItem() || recipe.getDamage() != result.getDamage())) {
                                    //If any matching recipes are no longer valid (so got removed), check if our set of matching recipes is now empty now
                                    if (matchingRecipes.isEmpty()) {
                                        //If it is exit due to there being no match
                                        return Collections.emptySet();
                                    }
                                }
                            }
                        }
                        if (!matchingRecipes.isEmpty()) {
                            //We have at least one matching recipe, so return the found recipes
                            return matchingRecipes;
                        }
                    }
                }
            }
        }
        return Collections.emptySet();
    }
}
