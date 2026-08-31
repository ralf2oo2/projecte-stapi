package net.ralf2oo2.projecte.capability;

import net.danygames2014.nyalib.capability.entity.EntityCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.ralf2oo2.projecte.config.Config;
import net.ralf2oo2.projecte.item.FireProtector;
import net.ralf2oo2.projecte.item.FlightProvider;
import net.ralf2oo2.projecte.item.StepAssister;
import net.ralf2oo2.projecte.listener.ItemListener;
import net.ralf2oo2.projecte.util.StackUtil;

public class InternalAbilitiesEntityCapability extends EntityCapability {
    private final PlayerEntity player;
    private boolean swrgOverride = false;
    private boolean gemArmorReady = false;
    private boolean hadFlightItem = false;
    private boolean wasFlyingGamemode = false;
    private boolean isFlyingGamemode = false;
    private boolean wasFlying = false;
    private int projectileCooldown = 0;
    private int gemChestCooldown = 0;

    // capabilities
    private boolean allowFlying;

    public InternalAbilitiesEntityCapability(PlayerEntity player) {
        this.player = player;
    }

    public void resetProjectileCooldown() {
        projectileCooldown = Config.MISCELANIOUS_CONFIG.projectileCooldown;
    }

    public int getProjectileCooldown() {
        return projectileCooldown;
    }

    public void resetGemCooldown() {
        gemChestCooldown = Config.MISCELANIOUS_CONFIG.gemChestCooldown;
    }

    public int getGemCooldown() {
        return gemChestCooldown;
    }

    public void setGemState(boolean state)
    {
        gemArmorReady = state;
    }

    public boolean getGemState()
    {
        return gemArmorReady;
    }

    public void tick()
    {
        if (projectileCooldown > 0)
        {
            projectileCooldown--;
        }

        if (gemChestCooldown > 0)
        {
            gemChestCooldown--;
        }

//        if (!shouldPlayerFly())
//        {
//            if (hadFlightItem)
//            {
//                if (this.allowFlying)
//                {
//                    PlayerHelper.updateClientServerFlight(player, false);
//                }
//
//                hadFlightItem = false;
//            }
//            wasFlyingGamemode = false;
//            wasFlying = false;
//        }
//        else
//        {
//            if (!hadFlightItem)
//            {
//                if (!player.capabilities.allowFlying)
//                {
//                    PlayerHelper.updateClientServerFlight(player, true);
//                }
//
//                hadFlightItem = true;
//            }
//            else if (wasFlyingGamemode && !isFlyingGamemode)
//            {
//                //Player was in a gamemode that allowed flight, but no longer is but they still should be allowed to fly
//                //Sync the fact to the client. Also passes wasFlying so that if they were flying previously,
//                //and are still allowed to the gamemode change doesn't force them out of it
//                PlayerHelper.updateClientServerFlight(player, true, wasFlying);
//            }
//            wasFlyingGamemode = isFlyingGamemode;
//            wasFlying = player.capabilities.isFlying;
//        }

//        if (!shouldPlayerResistFire())
//        {
//            if (player.isImmuneToFire())
//            {
//                player.isImmuneToFire = false;
//            }
//        }
//        else
//        {
//            if (!player.isImmuneToFire())
//            {
//                player.isImmuneToFire = true;
//            }
//        }

//        if (!shouldPlayerStep())
//        {
//            if (player.stepHeight > 0.6F)
//            {
//                PlayerHelper.updateClientServerStepHeight(player, 0.6F);
//            }
//        }
//        else
//        {
//            if (player.stepHeight < 1.0F)
//            {
//                PlayerHelper.updateClientServerStepHeight(player, 1.0F);
//            }
//        }
    }

    public void onDimensionChange()
    {
        // Resend everything needed on clientside (all except fire resist)
//        PlayerHelper.updateClientServerFlight(player, player.capabilities.allowFlying);
//        PlayerHelper.updateClientServerStepHeight(player, shouldPlayerStep() ? 1.0F : 0.6F);
    }

    private boolean shouldPlayerFly()
    {
        if (!hasSwrg())
        {
            disableSwrgFlightOverride();
        }

        isFlyingGamemode = false; // player.capabilities.isCreativeMode || player.isSpectator(); TODO: possible BHCreative integration or future replacement
        if (isFlyingGamemode || swrgOverride)
        {
            return true;
        }

        for (ItemStack stack : player.inventory.armor)
        {
            if (!StackUtil.isEmpty(stack)
                        && stack.getItem() instanceof FlightProvider flightProvider
                        && flightProvider.canProvideFlight(stack, player))
            {
                return true;
            }
        }

        for (int i = 0; i <= 8; i++)
        {
            ItemStack stack = player.inventory.getStack(i);

            if (!StackUtil.isEmpty(stack)
                        && stack.getItem() instanceof FlightProvider flightProvider
                        && flightProvider.canProvideFlight(stack, player))
            {
                return true;
            }
        }

        return false;
    }

    private boolean shouldPlayerResistFire()
    {
        // TODO: another creative check
//        if (player.capabilities.isCreativeMode)
//        {
//            return true;
//        }


        for (ItemStack stack : player.inventory.armor)
        {
            if (!StackUtil.isEmpty(stack)
                        && stack.getItem() instanceof FireProtector fireProtector
                        && fireProtector.canProtectAgainstFire(stack, player))
            {
                return true;
            }
        }

        for (int i = 0; i <= 8; i++)
        {
            ItemStack stack = player.inventory.getStack(i);

            if (!StackUtil.isEmpty(stack)
                        && stack.getItem() instanceof FireProtector fireProtector
                        && fireProtector.canProtectAgainstFire(stack, player))
            {
                return true;
            }
        }

        return false;
    }

    private boolean shouldPlayerStep()
    {
        for (ItemStack stack : player.inventory.armor)
        {
            if (!StackUtil.isEmpty(stack)
                        && stack.getItem() instanceof StepAssister stepAssister
                        && stepAssister.canAssistStep(stack, player))
            {
                return true;
            }
        }

        for (int i = 0; i <= 8; i++)
        {
            ItemStack stack = player.inventory.getStack(i);

            if (!StackUtil.isEmpty(stack)
                        && stack.getItem() instanceof StepAssister stepAssister
                        && stepAssister.canAssistStep(stack, player))
            {
                return true;
            }
        }

        return false;
    }

    private boolean hasSwrg()
    {
        for (int i = 0; i <= 8; i++)
        {
            if (!StackUtil.isEmpty(player.inventory.main[i]) && player.inventory.main[i].getItem() == ItemListener.swrg)
            {
                return true;
            }
        }

        return false;
    }

    public void enableSwrgFlightOverride()
    {
        swrgOverride = true;
    }

    public void disableSwrgFlightOverride()
    {
        swrgOverride = false;
    }
}
