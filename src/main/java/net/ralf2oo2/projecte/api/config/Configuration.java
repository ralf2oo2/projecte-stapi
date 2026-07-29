package net.ralf2oo2.projecte.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.ralf2oo2.projecte.ProjectE;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Configuration {
    private final File configFile;
    private JsonObject root;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private boolean isDirty = false;

    public Configuration() {
        configFile = null;
    }

    public Configuration(File configFile) {
        this.configFile = configFile;
        load();
    }

    public void load() {
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                root = gson.fromJson(reader, JsonObject.class);
                if (root == null) root = new JsonObject();
            } catch (Exception e) {
                ProjectE.LOGGER.error("Failed to load config file: {}", configFile.getName(), e);
                root = new JsonObject();
            }
        } else {
            root = new JsonObject();
        }
    }

    public void save() {
        if (!isDirty) return;

        configFile.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(root, writer);
            isDirty = false;
        } catch (IOException e) {
            ProjectE.LOGGER.error("Failed to save config file: {}", configFile.getName(), e);
        }
    }

    private JsonObject getOrCreateCategory(String category) {
        if (category == null || category.isEmpty()) {
            return root;
        }

        if (!root.has(category)) {
            root.add(category, new JsonObject());
            isDirty = true;
        }
        return root.getAsJsonObject(category);
    }

    public boolean getBoolean(String key, String category, boolean defaultValue, String comment) {
        JsonObject catNode = getOrCreateCategory(category);

        if (catNode.has(key)) {
            return catNode.get(key).getAsBoolean();
        } else {
            catNode.addProperty(key, defaultValue);
            isDirty = true;
            return defaultValue;
        }
    }

    public int getInt(String key, String category, int defaultValue, String comment) {
        JsonObject catNode = getOrCreateCategory(category);

        if (catNode.has(key)) {
            return catNode.get(key).getAsInt();
        } else {
            catNode.addProperty(key, defaultValue);
            isDirty = true;
            return defaultValue;
        }
    }
}
