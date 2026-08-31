package net.ralf2oo2.projecte.config;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class PedestalConfig {
    @ConfigEntry(name = "Archangel Pedestal Cooldown", description = "Delay between Archangel Smite shooting arrows while in the pedestal.", multiplayerSynced = true, minValue = -1, maxValue = Integer.MAX_VALUE)
    public Integer archangelPedCooldown = 40;

    @ConfigEntry(name = "Body Pedestal Cooldown", description = "Delay between Body Stone healing 0.5 shanks while in the pedestal.", multiplayerSynced = true, minValue = -1, maxValue = Integer.MAX_VALUE)
    public Integer bodyPedCooldown = 10;

    @ConfigEntry(name = "Evertide Pedestal Cooldown", description = "Delay between Evertide Amulet trying to start rain while in the pedestal.", multiplayerSynced = true, minValue = -1, maxValue = Integer.MAX_VALUE)
    public Integer evertidePedCooldown = 20;

    @ConfigEntry(name = "Harvest Pedestal Cooldown", description = "Delay between Harvest Goddess trying to grow and harvest while in the pedestal.", multiplayerSynced = true, minValue = -1, maxValue = Integer.MAX_VALUE)
    public Integer harvestPedCooldown = 10;

    @ConfigEntry(name = "Ignite Pedestal Cooldown", description = "Delay between Ignition Ring trying to light entities on fire while in the pedestal.", multiplayerSynced = true, minValue = -1, maxValue = Integer.MAX_VALUE)
    public Integer ignitePedCooldown = 40;

    @ConfigEntry(name = "Life Pedestal Cooldown", description = "Delay between Life Stone healing both food and hunger by 0.5 shank/heart while in the pedestal.", multiplayerSynced = true, minValue = -1, maxValue = Integer.MAX_VALUE)
    public Integer lifePedCooldown = 5;

    @ConfigEntry(name = "Repair Pedestal Cooldown", description = "Delay between Talisman of Repair trying to repair player items while in the pedestal.", multiplayerSynced = true, minValue = -1, maxValue = Integer.MAX_VALUE)
    public Integer repairPedCooldown = 20;

    @ConfigEntry(name = "SWRG Pedestal Cooldown", description = "Delay between SWRG trying to smite mobs while in the pedestal.", multiplayerSynced = true, minValue = -1, maxValue = Integer.MAX_VALUE)
    public Integer swrgPedCooldown = 70;

    @ConfigEntry(name = "Soul Pedestal Cooldown", description = "Delay between Soul Stone healing 0.5 hearts while in the pedestal.", multiplayerSynced = true, minValue = -1, maxValue = Integer.MAX_VALUE)
    public Integer soulPedCooldown = 10;

    @ConfigEntry(name = "Volcanite Pedestal Cooldown", description = "Delay between Volcanite Amulet trying to stop rain while in the pedestal.", multiplayerSynced = true, minValue = -1, maxValue = Integer.MAX_VALUE)
    public Integer volcanitePedCooldown = 20;

    @ConfigEntry(name = "Zero Pedestal Cooldown", description = "Delay between Zero Ring trying to extinguish entities and freezing ground while in the pedestal.", multiplayerSynced = true, minValue = -1, maxValue = Integer.MAX_VALUE)
    public Integer zeroPedCooldown = 40;
}
