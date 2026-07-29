package net.ralf2oo2.projecte.config;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class MiscelaniousConfig {
    @ConfigEntry(name = "Debug logging", description = "Enable a more verbose debug logging")
    public Boolean debugLogging = false;

    @ConfigEntry(name = "EMC Tooltips", description = "Show the EMC value as a tooltip on items and blocks")
    public Boolean emcToolTips = true;

    @ConfigEntry(name = "Stat Tooltips", description = "Show stats as tooltips for various ProjectE blocks")
    public Boolean statToolTips = true;

    @ConfigEntry(name = "Pedestal Tooltips", description = "Show DM pedestal functions in item tooltips")
    public Boolean pedestalToolTips = true;

    @ConfigEntry(name = "Pulsating overlay", description = "The Philosopher's Stone overlay softly pulsates")
    public Boolean pulsatingOverlay = false;

    @ConfigEntry(name = "Unsafe keybinds", description = "False requires your hand be empty for Gem Armor Offensive Abilities to be readied or triggered")
    public Boolean unsafeKeyBinds = false;

    @ConfigEntry(name = "Projectile cooldown", description = "A cooldown (in ticks) for firing projectiles", minValue = 0)
    public Integer projectileCooldown = 0;

    @ConfigEntry(name = "Gem chestplate cooldown", description = "A cooldown (in ticks) for Gem Chestplate explosion", minValue = 0)
    public Integer gemChestCooldown = 0;
}
