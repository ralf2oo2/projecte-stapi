package net.ralf2oo2.projecte.config;

import net.glasslauncher.mods.gcapi3.api.ConfigRoot;

public class Config {
    @ConfigRoot(value = "difficulty", visibleName = "Difficulty")
    public static final DifficultyConfig DIFFICULTY_CONFIG = new DifficultyConfig();
}
