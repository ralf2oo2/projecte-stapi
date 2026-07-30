package net.ralf2oo2.projecte.capability.provider;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.danygames2014.nyalib.capability.entity.EntityCapabilityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.ralf2oo2.projecte.api.capability.AlchemicalBagEntityCapability;
import net.ralf2oo2.projecte.api.capability.KnowledgeEntityCapability;
import net.ralf2oo2.projecte.capability.DefaultAlchemicalBagEntityCapability;
import net.ralf2oo2.projecte.capability.DefaultKnowledgeEntityCapability;
import org.jetbrains.annotations.Nullable;

public class DefaultAlchemicalBagEntityCapabilityProvider extends EntityCapabilityProvider<AlchemicalBagEntityCapability> {
    Object2ObjectOpenHashMap<PlayerEntity, DefaultAlchemicalBagEntityCapability> capabilityCache = new Object2ObjectOpenHashMap<>();

    @Override
    public @Nullable AlchemicalBagEntityCapability getCapability(Entity entity) {
        if(entity instanceof PlayerEntity player) {
            if(!capabilityCache.containsKey(player)) {
                capabilityCache.put(player, new DefaultAlchemicalBagEntityCapability());
            }
            return capabilityCache.get(player);
        }
        return null;
    }
}
