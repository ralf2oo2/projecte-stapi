package net.ralf2oo2.projecte.event;

import net.mine_diver.unsafeevents.Event;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerKnowledgeChangeEvent extends Event {
    public PlayerEntity player;

    public PlayerKnowledgeChangeEvent(PlayerEntity player) {
        this.player = player;
    }
}
