package net.ralf2oo2.projecte.listener;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.entity.EntityRegisterEvent;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.entity.*;

public class EntityListener {
    @EventListener
    public void registerEntities(EntityRegisterEvent event) {
        event.register(ProjectE.NAMESPACE.id("nova_catalyst"), PrimedNovaCatalystEntity.class);
        event.register(ProjectE.NAMESPACE.id("nova_cataclysm"), PrimedNovaCataclysmEntity.class);
        event.register(ProjectE.NAMESPACE.id("swrg_projectile"), SWRGProjectileEntity.class);
        event.register(ProjectE.NAMESPACE.id("lens_projectile"), LensProjectileEntity.class);
        event.register(ProjectE.NAMESPACE.id("mob_randomizer_projectile"), MobRandomizerProjectileEntity.class);
    }
}
