package net.ralf2oo2.projecte.emc.mapper;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmeltingRecipeManager;
import net.ralf2oo2.projecte.api.config.Configuration;
import net.ralf2oo2.projecte.emc.IngredientMap;
import net.ralf2oo2.projecte.emc.collector.MappingCollector;
import net.ralf2oo2.projecte.emc.json.NSSItem;
import net.ralf2oo2.projecte.emc.json.NormalizedSimpleStack;
import net.ralf2oo2.projecte.util.ConfigHelper;
import net.ralf2oo2.projecte.util.StackUtil;

import java.util.Map;

public class SmeltingMapper implements EMCMapper<NormalizedSimpleStack, Long>{
    @Override
    public void addMappings(MappingCollector<NormalizedSimpleStack, Long> mapper, CommentedConfig config) {
        Map<?, ItemStack> smeltingMap = SmeltingRecipeManager.getInstance().getRecipes();

        for (Map.Entry<?, ItemStack> entry : smeltingMap.entrySet()) {
            Object inputRaw = entry.getKey();
            ItemStack output = entry.getValue();

            if (StackUtil.isEmpty(output)) {
                continue;
            }

            ItemStack input;
            if (inputRaw instanceof Integer itemID) {
                input = new ItemStack(itemID, 1, 0);
            } else if (inputRaw instanceof ItemStack inputStack) {
                input = inputStack;
            } else {
                continue;
            }

            if (StackUtil.isEmpty(input)) {
                continue;
            }

            IngredientMap<NormalizedSimpleStack> map = new IngredientMap<>();
            NormalizedSimpleStack normInput = NSSItem.create(input);
            NormalizedSimpleStack normOutput = NSSItem.create(output);

            map.addIngredient(normInput, input.count);
            mapper.addConversion(output.count, normOutput, map.getMap());

            if (ConfigHelper.getBoolean(config, "doBackwardsMapping", "", false, "If X has a value and is smelted from Y, Y will get a value too. This is an experimental thing and might result in Mappings you did not expect/want to happen.")) {
                IngredientMap<NormalizedSimpleStack> backMap = new IngredientMap<>();
                backMap.addIngredient(normOutput, output.count);
                mapper.addConversion(input.count, normInput, backMap.getMap());
            }
        }
    }

    @Override
    public String getName() {
        return "SmeltingMapper";
    }

    @Override
    public String getDescription() {
        return "Add Conversions for standard Smelting Recipes";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
