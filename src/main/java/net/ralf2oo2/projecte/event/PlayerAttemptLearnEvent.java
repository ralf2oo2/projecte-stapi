package net.ralf2oo2.projecte.event;

import net.mine_diver.unsafeevents.Event;
import net.mine_diver.unsafeevents.event.Cancelable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired on both the server and client when a player is attempting to learn a new item
 * This event is cancelable
 * This event is fired on StationAPI#EVENT_BUS
 */
@Cancelable
public class PlayerAttemptLearnEvent extends Event {
    private final PlayerEntity player;
    private final ItemStack stack;

    public PlayerAttemptLearnEvent(@NotNull PlayerEntity entityPlayer, @NotNull ItemStack stack)
    {
        player = entityPlayer;
        this.stack = stack;
    }

    /**
     * @return The player who is attempting to learn a new item.
     */
    @NotNull
    public PlayerEntity getPlayer()
    {
        return player;
    }

    /**
     * @return The stack that the player is trying to learn.
     */
    @NotNull
    public ItemStack getStack()
    {
        return stack;
    }
}
