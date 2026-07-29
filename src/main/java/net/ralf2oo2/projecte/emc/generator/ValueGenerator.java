package net.ralf2oo2.projecte.emc.generator;

import java.util.Map;

/**
 * Defines something that can simply yield a mapping of values.
 * @param <T> The key type
 * @param <V> The value type
 */
public interface ValueGenerator<T, V extends Comparable<V>>
{
    Map<T, V> generateValues();
}
