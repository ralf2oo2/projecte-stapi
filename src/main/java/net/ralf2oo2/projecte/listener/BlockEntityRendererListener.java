package net.ralf2oo2.projecte.listener;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.block.entity.BlockEntityRendererRegisterEvent;
import net.ralf2oo2.projecte.block.entity.DarkMatterPedestalBlockEntity;
import net.ralf2oo2.projecte.client.render.block.entity.DarkMatterPedestalBlockEntityRenderer;

public class BlockEntityRendererListener {

    @EventListener
    public void registerBlockEntityRenderers(BlockEntityRendererRegisterEvent event) {
        event.renderers.put(DarkMatterPedestalBlockEntity.class, new DarkMatterPedestalBlockEntityRenderer());
    }
}
