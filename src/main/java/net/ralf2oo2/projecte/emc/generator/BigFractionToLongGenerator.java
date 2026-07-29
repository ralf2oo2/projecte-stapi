package net.ralf2oo2.projecte.emc.generator;

import org.apache.commons.math3.fraction.BigFraction;

import java.util.HashMap;
import java.util.Map;

/**
 * Composes another IValueGenerator, and truncates all fractional values towards 0.
 * @param <T> The type we are generating values for
 */
public class BigFractionToLongGenerator<T> implements ValueGenerator<T, Long>
{
    private final ValueGenerator<T, BigFraction> inner;

    public BigFractionToLongGenerator(ValueGenerator<T, BigFraction> inner) {
        this.inner = inner;
    }

    @Override
    public Map<T, Long> generateValues()
    {
        Map<T, BigFraction> innerResult = inner.generateValues();
        Map<T, Long> myResult = new HashMap<>();
        for (Map.Entry<T, BigFraction> entry: innerResult.entrySet())
        {
            BigFraction value = entry.getValue();
            if (value.longValue() > 0)
            {
                myResult.put(entry.getKey(), value.longValue());
            }
        }
        return myResult;
    }
}
