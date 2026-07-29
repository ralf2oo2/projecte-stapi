package net.ralf2oo2.projecte.emc.mapper.customconversion.json;

import com.google.gson.annotations.SerializedName;
import net.ralf2oo2.projecte.emc.json.NormalizedSimpleStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixedValues
{
    @SerializedName("before")
    public Map<NormalizedSimpleStack, Long> setValueBefore = new HashMap<>();
    @SerializedName("after")
    public Map<NormalizedSimpleStack, Long> setValueAfter = new HashMap<>();
    public List<CustomConversion> conversion = new ArrayList<>();
}
