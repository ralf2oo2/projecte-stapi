package net.ralf2oo2.projecte.event;

import net.mine_diver.unsafeevents.Event;
import net.mine_diver.unsafeevents.event.Cancelable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired on both the server and client when a player is attempting to place an item in the condenser.
 * This event is cancelable
 * This event is fired on StationAPI#EVENT_BUS
 */
@Cancelable
public class PlayerAttemptCondenserSetEvent extends Event {
    private final PlayerEntity player;
    private final ItemStack stack;

    public PlayerAttemptCondenserSetEvent(@NotNull PlayerEntity player, @Nullable ItemStack stack) {
        this.player = player;
        this.stack = stack;
    }

    /**
     * @return The player who is attempting to put in the condenser slot.
     */
    @NotNull
    public PlayerEntity getPlayer()
    {
        return player;
    }

    /**
     * @return The stack that the player is trying to learn.
     */
    @Nullable
    public ItemStack getStack()
    {
        return stack;
    }
}
