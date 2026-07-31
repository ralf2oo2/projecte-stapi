package net.ralf2oo2.projecte.block.entity;

import net.ralf2oo2.projecte.util.Constants;

public class EnergyCollectorMK3BlockEntity extends EnergyCollectorMK1BlockEntity{
    public EnergyCollectorMK3BlockEntity() {
        super(Constants.COLLECTOR_MK3_MAX, Constants.COLLECTOR_MK3_GEN);
    }

    @Override
    protected int getInvSize() {
        return 16;
    }
}
