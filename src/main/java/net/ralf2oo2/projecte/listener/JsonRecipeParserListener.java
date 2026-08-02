package net.ralf2oo2.projecte.listener;

import com.google.gson.Gson;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.registry.JsonRecipeParserRegistryEvent;
import net.modificationstation.stationapi.api.item.json.JsonItemKey;
import net.modificationstation.stationapi.api.recipe.CraftingRegistry;
import net.modificationstation.stationapi.impl.recipe.JsonCraftingShapeless;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.util.CraftingRegistryHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.function.Function;

public class JsonRecipeParserListener {

    @EventListener
    public void registerJsonRecipeParsers(JsonRecipeParserRegistryEvent event) {
        event.register(ProjectE.NAMESPACE)
                    .accept("crafting_shapeless_kleinstar", JsonRecipeParserListener::parseKleinStarRecipes);
    }

    private static void parseKleinStarRecipes(URL recipe) {
        JsonCraftingShapeless json;
        try {
            json = new Gson().fromJson(new BufferedReader(new InputStreamReader(recipe.openStream())), JsonCraftingShapeless.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JsonItemKey[] ingredients = json.getIngredients();
        Object[] stacks = new Object[json.getIngredients().length];
        for (int i = 0; i < ingredients.length; i++)
            stacks[i] = ingredients[i].get().map(Function.identity(), Function.identity());
        try {
            CraftingRegistryHelper.addKleinStarShapelessRecipe(json.getResult().getItemStack(), stacks);
        } catch (NullPointerException e) {
            throw new RuntimeException("Recipe: " + recipe, e);
        }
    }
}
