package net.ralf2oo2.projecte.listener;

import net.danygames2014.nyalib.event.EntityCapabilityClassRegisterEvent;
import net.danygames2014.nyalib.event.EntityCapabilityProviderRegisterEvent;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.api.capability.AlchemicalBagEntityCapability;
import net.ralf2oo2.projecte.api.capability.KnowledgeEntityCapability;
import net.ralf2oo2.projecte.capability.InternalAbilitiesEntityCapability;
import net.ralf2oo2.projecte.capability.InternalTimersEntityCapability;
import net.ralf2oo2.projecte.capability.provider.DefaultAlchemicalBagEntityCapabilityProvider;
import net.ralf2oo2.projecte.capability.provider.DefaultKnowledgeEntityCapabilityProvider;
import net.ralf2oo2.projecte.capability.provider.InternalAbilitiesCapabilityProvider;
import net.ralf2oo2.projecte.capability.provider.InternalTimersCapabilityProvider;

public class CapabilityListener {
    @EventListener
    public void registerEntityCapabilityProvider(EntityCapabilityProviderRegisterEvent event) {
        event.register(ProjectE.NAMESPACE.id("knowledge"), new DefaultKnowledgeEntityCapabilityProvider());
        event.register(ProjectE.NAMESPACE.id("alchemical_bag"), new DefaultAlchemicalBagEntityCapabilityProvider());
        event.register(ProjectE.NAMESPACE.id("internal_abilities"), new InternalAbilitiesCapabilityProvider());
        event.register(ProjectE.NAMESPACE.id("internal_timers"), new InternalTimersCapabilityProvider());
    }

    @EventListener
    public void registerEntityCapabilityClass(EntityCapabilityClassRegisterEvent event) {
        event.register(ProjectE.NAMESPACE.id("knowledge"), KnowledgeEntityCapability.class);
        event.register(ProjectE.NAMESPACE.id("alchemical_bag"), AlchemicalBagEntityCapability.class);
        event.register(ProjectE.NAMESPACE.id("internal_abilities"), InternalAbilitiesEntityCapability.class);
        event.register(ProjectE.NAMESPACE.id("internal_timers"), InternalTimersEntityCapability.class);
    }
}
