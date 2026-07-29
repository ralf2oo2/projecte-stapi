package net.ralf2oo2.projecte.util;

import com.electronwill.nightconfig.core.CommentedConfig;
import org.jetbrains.annotations.NotNull;

public class ConfigHelper {

    public static boolean getBoolean(@NotNull CommentedConfig config, String key, String category, boolean defaultValue, String comment) {
        return getBoolean(config, path(category, key), defaultValue, comment);
    }

    public static boolean getBoolean(@NotNull CommentedConfig config, @NotNull String path, boolean defaultValue, String comment) {
        if (!config.contains(path)) {
            config.set(path, defaultValue);
        }

        if (comment != null && !comment.isEmpty()) {
            config.setComment(path, comment + " [Default: " + defaultValue + "]");
        }

        return config.getOrElse(path, defaultValue);
    }

    public static int getInt(@NotNull CommentedConfig config, String key, String category, int defaultValue, String comment) {
        return getInt(config, path(category, key), defaultValue, comment);
    }

    public static int getInt(@NotNull CommentedConfig config, @NotNull String path, int defaultValue, String comment) {
        if (!config.contains(path)) {
            config.set(path, defaultValue);
        }

        if (comment != null && !comment.isEmpty()) {
            config.setComment(path, comment + " [Default: " + defaultValue + "]");
        }

        return config.getIntOrElse(path, defaultValue);
    }

    public static int getClampedInt(@NotNull CommentedConfig config, String key, String category, int defaultValue, int minValue, int maxValue, String comment) {
        return getClampedInt(config, path(category, key), defaultValue, minValue, maxValue, comment);
    }

    public static int getClampedInt(@NotNull CommentedConfig config, @NotNull String path, int defaultValue, int minValue, int maxValue, String comment) {
        if (!config.contains(path)) {
            int clampedDefault = Math.max(minValue, Math.min(maxValue, defaultValue));
            config.set(path, clampedDefault);
        }

        if (comment != null) {
            config.setComment(path, comment + " [Range: " + minValue + " ~ " + maxValue + ", Default: " + defaultValue + "]");
        }

        int rawValue = config.getIntOrElse(path, defaultValue);
        return Math.max(minValue, Math.min(maxValue, rawValue));
    }

    public static String getString(@NotNull CommentedConfig config, String key, String category, @NotNull String defaultValue, String comment, String[] validValues) {
        return getString(config, path(category, key), defaultValue, validValues, comment);
    }

    public static String getString(@NotNull CommentedConfig config, @NotNull String path, @NotNull String defaultValue, @NotNull String[] validValues, String comment) {
        if (!config.contains(path)) {
            config.set(path, defaultValue);
        }

        if (comment != null && validValues != null && validValues.length > 0) {
            config.setComment(path, comment + " [Valid options: " + String.join(", ", validValues) + "]");
        }

        String val = config.getOrElse(path, defaultValue);
        if (validValues != null && validValues.length > 0) {
            for (String valid : validValues) {
                if (valid.equalsIgnoreCase(val)) {
                    return val;
                }
            }
            return defaultValue;
        }
        return val;
    }

    private static String path(String category, String key) {
        if (category == null || category.isEmpty()) return key;
        return category + "." + key;
    }
}
