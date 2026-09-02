package net.ralf2oo2.projecte.capability.provider;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.danygames2014.nyalib.capability.entity.EntityCapabilityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.ralf2oo2.projecte.capability.InternalAbilitiesEntityCapability;
import net.ralf2oo2.projecte.capability.InternalTimersEntityCapability;
import org.jetbrains.annotations.Nullable;

public class InternalTimersCapabilityProvider extends EntityCapabilityProvider<InternalTimersEntityCapability> {
    Object2ObjectOpenHashMap<PlayerEntity, InternalTimersEntityCapability> capabilityCache = new Object2ObjectOpenHashMap<>();

    @Override
    public @Nullable InternalTimersEntityCapability getCapability(Entity entity) {
        if(entity instanceof PlayerEntity player) {
            if(!capabilityCache.containsKey(player)) {
                capabilityCache.put(player, new InternalTimersEntityCapability());
            }
            return capabilityCache.get(player);
        }
        return null;
    }
}
