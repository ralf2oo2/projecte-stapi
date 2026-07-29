package net.ralf2oo2.projecte.emc.mapper.customconversion.json;

import net.ralf2oo2.projecte.emc.json.NormalizedSimpleStack;

import java.util.HashMap;
import java.util.Map;

public class CustomConversion
{
    public int count = 1;
    public NormalizedSimpleStack output;
    public Map<NormalizedSimpleStack, Integer> ingredients;
    public transient boolean evalTAG = false;

    public static CustomConversion getFor(int count, NormalizedSimpleStack output, Map<NormalizedSimpleStack, Integer> ingredients) {
        CustomConversion conversion = new CustomConversion();
        conversion.count = count;
        conversion.output = output;
        conversion.ingredients = new HashMap<>();
        conversion.ingredients.putAll(ingredients);
        return conversion;
    }

    @Override
    public String toString() {
        return "{" + count + " * " + output + " = " + ingredients.toString() + "}";
    }
}
