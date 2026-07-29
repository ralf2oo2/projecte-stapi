package net.ralf2oo2.projecte.emc.collector;

import net.ralf2oo2.projecte.emc.arithmetic.ValueArithmetic;

import java.util.Map;

public interface ExtendedMappingCollector<T, V extends Comparable<V>, A extends ValueArithmetic> extends MappingCollector<T, V>
{
    void addConversion(int outnumber, T output, Map<T, Integer> ingredientsWithAmount, A arithmeticForConversion);

    void addConversion(int outnumber, T output, Iterable<T> ingredients, A arithmeticForConversion);

    A getArithmetic();
}
