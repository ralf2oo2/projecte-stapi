package net.ralf2oo2.projecte.emc.mapper;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.mojang.datafixers.util.Either;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.CraftingRecipeManager;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.modificationstation.stationapi.api.tag.TagKey;
import net.modificationstation.stationapi.impl.recipe.StationShapedRecipe;
import net.modificationstation.stationapi.impl.recipe.StationShapelessRecipe;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.emc.IngredientMap;
import net.ralf2oo2.projecte.emc.collector.MappingCollector;
import net.ralf2oo2.projecte.emc.json.NSSFake;
import net.ralf2oo2.projecte.emc.json.NSSItem;
import net.ralf2oo2.projecte.emc.json.NSSTag;
import net.ralf2oo2.projecte.emc.json.NormalizedSimpleStack;
import net.ralf2oo2.projecte.mixin.ShapedRecipeAccessor;
import net.ralf2oo2.projecte.mixin.ShapelessRecipeAccessor;
import net.ralf2oo2.projecte.util.StackUtil;

import java.util.*;

public class CraftingMapper implements EMCMapper<NormalizedSimpleStack, Long>{
    private final Set<Class<?>> canNotMap = new HashSet<>();
    private final Map<Class<?>, Integer> recipeCount = new HashMap<>();

    @Override
    public String getName() {
        return "CraftingMapper";
    }

    @Override
    public String getDescription() {
        return "Add Conversions for Crafting Recipes gathered from CraftingManager";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void addMappings(MappingCollector<NormalizedSimpleStack, Long> mapper, CommentedConfig config) {
        recipeCount.clear();
        canNotMap.clear();

        List<CraftingRecipe> recipes = (List<CraftingRecipe>) CraftingRecipeManager.getInstance().getRecipes();

        nextRecipe:
        for (CraftingRecipe recipe : recipes) {
            if (recipe == null) continue;

            ItemStack recipeOutput = recipe.getOutput();
            if (StackUtil.isEmpty(recipeOutput)) continue;

            NormalizedSimpleStack recipeOutputNorm = NSSItem.create(recipeOutput);

            CraftingIngredients variation = getIngredientsForRecipe(recipe);

            if (variation == null) {
                if (canNotMap.add(recipe.getClass())) {
                    ProjectE.LOGGER.warn("Cannot map Crafting Recipes with Type: {}", recipe.getClass().getName());
                }
                continue;
            }

            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();

            for (ItemStack stack : variation.fixedIngredients) {
                if (StackUtil.isEmpty(stack)) continue;

                try {
                    Item containerItem = getReturnItem(stack);
                    if (containerItem != null) {
                        ingredientMap.addIngredient(NSSItem.create(containerItem), -1);
                    }

                    NormalizedSimpleStack normStack = createNormalizedStack(stack);
                    if (normStack != null) {
                        ingredientMap.addIngredient(normStack, 1);
                    }
                } catch (Exception e) {
                    ProjectE.LOGGER.fatal("Exception in CraftingMapper when parsing Recipe Ingredients: RecipeType: {}, Ingredient: {}", recipe.getClass().getName(), stack, e);
                    continue nextRecipe;
                }
            }

            for (NormalizedSimpleStack tagStack : variation.tagIngredients) {
                ingredientMap.addIngredient(tagStack, 1);
            }

            for (Iterable<ItemStack> multiIngredient : variation.multiIngredients) {
                NormalizedSimpleStack dummy = NSSFake.create(multiIngredient.toString());
                ingredientMap.addIngredient(dummy, 1);

                for (ItemStack stack : multiIngredient) {
                    if (StackUtil.isEmpty(stack)) continue;

                    IngredientMap<NormalizedSimpleStack> groupIngredientMap = new IngredientMap<>();
                    Item containerItem = getReturnItem(stack);
                    if (containerItem != null) {
                        groupIngredientMap.addIngredient(NSSItem.create(containerItem), -1);
                    }
                    groupIngredientMap.addIngredient(NSSItem.create(stack), 1);

                    mapper.addConversion(1, dummy, groupIngredientMap.getMap());
                }
            }

            mapper.addConversion(recipeOutput.count, recipeOutputNorm, ingredientMap.getMap());

            recipeCount.put(recipe.getClass(), recipeCount.getOrDefault(recipe.getClass(), 0) + 1);
        }

        ProjectE.LOGGER.info("CraftingMapper Statistics:");
        for (Map.Entry<Class<?>, Integer> entry : recipeCount.entrySet()) {
            ProjectE.LOGGER.info("Found {} Recipes of Type {}", entry.getValue(), entry.getKey().getName());
        }
    }

    private CraftingIngredients getIngredientsForRecipe(CraftingRecipe recipe) {
        List<ItemStack> fixedInputs = new ArrayList<>();
        List<NormalizedSimpleStack> tagInputs = new ArrayList<>();

        if (recipe instanceof StationShapedRecipe stationShaped) {
            Either<TagKey<Item>, ItemStack>[] grid = stationShaped.getGrid();
            if (grid != null) {
                parseStapiEitherArray(grid, fixedInputs, tagInputs);
                return new CraftingIngredients(fixedInputs, tagInputs, Collections.emptyList());
            }
        }

        else if (recipe instanceof StationShapelessRecipe stationShapeless) {
            Either<TagKey<Item>, ItemStack>[] ingredients = stationShapeless.getIngredients();
            if (ingredients != null) {
                parseStapiEitherArray(ingredients, fixedInputs, tagInputs);
                return new CraftingIngredients(fixedInputs, tagInputs, Collections.emptyList());
            }
        }

        else if (recipe instanceof ShapedRecipe shaped) {
            ItemStack[] ingredients = ((ShapedRecipeAccessor) shaped).getInput();
            if (ingredients != null) {
                for (ItemStack stack : ingredients) {
                    if (!StackUtil.isEmpty(stack)) {
                        fixedInputs.add(stack.copy());
                    }
                }
                return new CraftingIngredients(fixedInputs, Collections.emptyList(), Collections.emptyList());
            }
        }

        else if (recipe instanceof ShapelessRecipe shapeless) {
            List<ItemStack> ingredients = ((ShapelessRecipeAccessor) shapeless).getInput();
            if (ingredients != null) {
                for (ItemStack stack : ingredients) {
                    if (!StackUtil.isEmpty(stack)) {
                        fixedInputs.add(stack.copy());
                    }
                }
                return new CraftingIngredients(fixedInputs, Collections.emptyList(), Collections.emptyList());
            }
        }
        return null;
    }

    private void parseStapiEitherArray(Either<TagKey<Item>, ItemStack>[] entries, List<ItemStack> fixedInputs, List<NormalizedSimpleStack> tagInputs) {
        for (Either<TagKey<Item>, ItemStack> entry : entries) {
            if (entry == null) continue;

            Optional<TagKey<Item>> tagOpt = entry.left();
            if (tagOpt.isPresent()) {
                TagKey<Item> tagKey = tagOpt.get();
                tagInputs.add(NSSTag.create(tagKey.id().toString()));
            } else {
                Optional<ItemStack> itemOpt = entry.right();
                if (itemOpt.isPresent()) {
                    ItemStack stack = itemOpt.get();
                    if (!StackUtil.isEmpty(stack)) {
                        ItemStack copy = stack.copy();
                        if (copy.getDamage() == -1 || copy.getDamage() == 32767) {
                            copy.setDamage(0);
                        }
                        fixedInputs.add(copy);
                    }
                }
            }
        }
    }

    private NormalizedSimpleStack createNormalizedStack(ItemStack stack) {
        if (StackUtil.isEmpty(stack)) return null;

        int damage = stack.getDamage();
        Item item = stack.getItem();

        if (damage == -1 || damage == 32767 || (item != null && !item.hasSubtypes())) {
            return NSSItem.create(item);
        }

        return NSSItem.create(stack);
    }

    private Item getReturnItem(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return null;
        if (stack.getItem().hasCraftingReturnItem()) {
            return stack.getItem().getCraftingReturnItem();
        }
        return null;
    }


    private static class CraftingIngredients {
        public final Iterable<ItemStack> fixedIngredients;
        public final Iterable<NormalizedSimpleStack> tagIngredients;
        public final Iterable<Iterable<ItemStack>> multiIngredients;

        public CraftingIngredients(
                Iterable<ItemStack> fixedIngredients,
                Iterable<NormalizedSimpleStack> tagIngredients,
                Iterable<Iterable<ItemStack>> multiIngredients
        ) {
            this.fixedIngredients = fixedIngredients;
            this.tagIngredients = tagIngredients;
            this.multiIngredients = multiIngredients;
        }
    }
}
