package net.ralf2oo2.projecte.listener;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.event.recipe.RecipeRegisterEvent;
import net.ralf2oo2.projecte.ProjectE;

public class RecipeListener {

    @EventListener
    public void sendCustomRecipeEvent(RecipeRegisterEvent event) {
        if(event.recipeId == RecipeRegisterEvent.Vanilla.CRAFTING_SHAPELESS.type()) {
            StationAPI.EVENT_BUS.post(RecipeRegisterEvent.builder().recipeId(ProjectE.NAMESPACE.id("crafting_shapeless_kleinstar")).build());
            StationAPI.EVENT_BUS.post(RecipeRegisterEvent.builder().recipeId(ProjectE.NAMESPACE.id("philosopher_stone_smelting")).build());
            StationAPI.EVENT_BUS.post(RecipeRegisterEvent.builder().recipeId(ProjectE.NAMESPACE.id("covalence_repair")).build());
        }
    }
}
