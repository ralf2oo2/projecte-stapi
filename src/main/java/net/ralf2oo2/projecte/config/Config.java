package net.ralf2oo2.projecte.config;

import net.glasslauncher.mods.gcapi3.api.ConfigRoot;

public class Config {
    @ConfigRoot(value = "difficulty", visibleName = "Difficulty")
    public static final DifficultyConfig DIFFICULTY_CONFIG = new DifficultyConfig();

    @ConfigRoot(value = "miscelanious", visibleName = "Miscelanious")
    public static final MiscelaniousConfig MISCELANIOUS_CONFIG = new MiscelaniousConfig();
}
