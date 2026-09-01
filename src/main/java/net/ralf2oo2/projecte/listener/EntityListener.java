package net.ralf2oo2.projecte.listener;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.entity.EntityRegisterEvent;
import net.ralf2oo2.projecte.ProjectE;
import net.ralf2oo2.projecte.entity.LensProjectileEntity;
import net.ralf2oo2.projecte.entity.PrimedNovaCataclysmEntity;
import net.ralf2oo2.projecte.entity.PrimedNovaCatalystEntity;
import net.ralf2oo2.projecte.entity.SWRGProjectileEntity;

public class EntityListener {
    @EventListener
    public void registerEntities(EntityRegisterEvent event) {
        event.register(ProjectE.NAMESPACE.id("nova_catalyst"), PrimedNovaCatalystEntity.class);
        event.register(ProjectE.NAMESPACE.id("nova_cataclysm"), PrimedNovaCataclysmEntity.class);
        event.register(ProjectE.NAMESPACE.id("swrg_projectile"), SWRGProjectileEntity.class);
        event.register(ProjectE.NAMESPACE.id("lens_projectile"), LensProjectileEntity.class);
    }
}
