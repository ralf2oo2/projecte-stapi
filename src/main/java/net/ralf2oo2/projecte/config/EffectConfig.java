package net.ralf2oo2.projecte.config;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class EffectConfig {
    @ConfigEntry(name = "Time pedestal bonus", description = "Bonus ticks given by the Watch of Flowing Time while in the pedestal. 0 = effectively no bonus.", minValue = 0, maxValue = 256, multiplayerSynced = true)
    public Integer timePedBonus = 18;

    @ConfigEntry(name = "Time pedestal mob slowness", description = "Factor the Watch of Flowing Time slows down mobs by while in the pedestal. Set to 1.0 for no slowdown.", minValue = 0, maxValue = 1, multiplayerSynced = true)
    public Float timePedMobSlowness = 0.10F;

//    public String[] timeWatchBlockBlacklist = {};
//    public String[] timeWatchTEBlacklist = {
//            "projecte:dm_pedestal"
//    };

    @ConfigEntry(name = "Interdiction mode", description = "If true the Interdiction Torch only affects hostile mobs. If false it affects all non blacklisted living entities.", minValue = 0, maxValue = 1, multiplayerSynced = true)
    public Boolean interdictionMode = true;
}
