package net.ralf2oo2.projecte.listener;

import net.danygames2014.nyalib.event.EntityCapabilityProviderRegisterEvent;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.capability.provider.DefaultAlchemicalBagEntityCapabilityProvider;
import net.ralf2oo2.projecte.capability.provider.DefaultKnowledgeEntityCapabilityProvider;

public class CapabilityListener {
    @EventListener
    public void registerEntityCapabilityProvider(EntityCapabilityProviderRegisterEvent event) {
        event.register(ProjectE.NAMESPACE.id("knowledge"), new DefaultKnowledgeEntityCapabilityProvider());
        event.register(ProjectE.NAMESPACE.id("alchemical_bag"), new DefaultAlchemicalBagEntityCapabilityProvider());
    }
}
