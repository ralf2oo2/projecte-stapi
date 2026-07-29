package net.ralf2oo2.projecte.capability.provider;

import net.danygames2014.nyalib.capability.entity.EntityCapabilityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.ralf2oo2.projecte.api.capability.KnowledgeEntityCapability;
import net.ralf2oo2.projecte.capability.DefaultKnowledgeEntityCapability;
import org.jetbrains.annotations.Nullable;

public class DefaultKnowledgeEntityCapabilityProvider extends EntityCapabilityProvider<KnowledgeEntityCapability> {
    @Override
    public @Nullable KnowledgeEntityCapability getCapability(Entity entity) {
        if(entity instanceof PlayerEntity player) {
            return new DefaultKnowledgeEntityCapability(player);
        }
        return null;
    }
}
