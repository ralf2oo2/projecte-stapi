package net.ralf2oo2.projecte.capability.provider;

import net.danygames2014.nyalib.capability.entity.EntityCapabilityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.ralf2oo2.projecte.api.capability.AlchemicalBagEntityCapability;
import net.ralf2oo2.projecte.api.capability.KnowledgeEntityCapability;
import net.ralf2oo2.projecte.capability.DefaultAlchemicalBagEntityCapability;
import net.ralf2oo2.projecte.capability.DefaultKnowledgeEntityCapability;
import org.jetbrains.annotations.Nullable;

public class DefaultAlchemicalBagEntityCapabilityProvider extends EntityCapabilityProvider<AlchemicalBagEntityCapability> {
    @Override
    public @Nullable AlchemicalBagEntityCapability getCapability(Entity entity) {
        if(entity instanceof PlayerEntity player) {
            return new DefaultAlchemicalBagEntityCapability();
        }
        return null;
    }
}
