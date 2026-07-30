package net.ralf2oo2.projecte.config;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class DifficultyConfig {
    @ConfigEntry(name = "Craftable Tome", description = "The Tome of Knowledge can be crafted.", multiplayerSynced = true)
    public Boolean craftableTome = false;

    @ConfigEntry(name = "Offensive abilities", description = "Set to false to disable Gem Armor offensive abilities (helmet zap and chestplate explosion)", multiplayerSynced = true)
    public Boolean offensiveAbilities = true;

    @ConfigEntry(name = "Katar death aura", description = "Amount of damage Katar 'C' key deals", minValue = 0, maxValue = Integer.MAX_VALUE, multiplayerSynced = true)
    public Float katarDeathAura = 1000F;

    @ConfigEntry(name = "Covalence loss", description = "Adjusting this ratio changes how much EMC is received when burning a item. For example setting this to 0.5 will return half of the EMC cost.", minValue = 0.1, maxValue = 1.0, multiplayerSynced = true)
    public Double covalenceLoss = 1.0;

    @ConfigEntry(name = "Covalence loss rounding", description = "How rounding occurs when Covalence Loss results in a burn value less than 1 EMC. If true the value will be rounded up to 1. If false the value will be rounded down to 0.", multiplayerSynced = true)
    public Boolean covalenceLossRounding = true;

    @ConfigEntry(name = "Disable transmutation search", description = "Disable searching and pages on transmutation", multiplayerSynced = true)
    public Boolean disableTransmutationSearch = false;
}
