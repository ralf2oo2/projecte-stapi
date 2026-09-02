package net.ralf2oo2.projecte.capability;

import net.danygames2014.nyalib.capability.entity.EntityCapability;

public class InternalTimersEntityCapability extends EntityCapability {
    private final Timer repair = new Timer();
    private final Timer heal = new Timer();
    private final Timer feed = new Timer();

    public void tick()
    {
        if (repair.shouldUpdate)
        {
            repair.tickCount++;
            repair.shouldUpdate = false;
        }

        if (heal.shouldUpdate)
        {
            heal.tickCount++;
            heal.shouldUpdate = false;
        }

        if (feed.shouldUpdate)
        {
            feed.tickCount++;
            feed.shouldUpdate = false;
        }
    }

    public void activateRepair()
    {
        repair.shouldUpdate = true;
    }

    public void activateHeal()
    {
        heal.shouldUpdate = true;
    }

    public void activateFeed()
    {
        feed.shouldUpdate = true;
    }

    public boolean canRepair()
    {
        if (repair.tickCount >= 19)
        {
            repair.tickCount = 0;
            repair.shouldUpdate = false;
            return true;
        }

        return false;
    }

    public boolean canHeal()
    {
        if (heal.tickCount >= 19)
        {
            heal.tickCount = 0;
            heal.shouldUpdate = false;
            return true;
        }

        return false;
    }

    public boolean canFeed()
    {
        if (feed.tickCount >= 19)
        {
            feed.tickCount = 0;
            feed.shouldUpdate = false;
            return true;
        }

        return false;
    }

    private static class Timer
    {
        public int tickCount = 0;
        public boolean shouldUpdate = false;
    }
}
