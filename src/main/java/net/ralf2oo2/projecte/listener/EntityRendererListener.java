package net.ralf2oo2.projecte.listener;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.render.entity.EntityRendererRegisterEvent;
import net.ralf2oo2.projecte.client.render.entity.PrimedNovaEntityRenderer;
import net.ralf2oo2.projecte.entity.PrimedNovaCataclysmEntity;
import net.ralf2oo2.projecte.entity.PrimedNovaCatalystEntity;

public class EntityRendererListener {
    @EventListener
    public void registerEntityRenderers(EntityRendererRegisterEvent event) {
        event.renderers.put(PrimedNovaCatalystEntity.class, new PrimedNovaEntityRenderer(BlockListener.novaCatalyst));
        event.renderers.put(PrimedNovaCataclysmEntity.class, new PrimedNovaEntityRenderer(BlockListener.novaCataclysm));
    }
}
