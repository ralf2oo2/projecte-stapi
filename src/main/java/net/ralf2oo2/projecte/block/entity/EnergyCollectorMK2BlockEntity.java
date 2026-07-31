package net.ralf2oo2.projecte.block.entity;

import net.ralf2oo2.projecte.util.Constants;

public class EnergyCollectorMK2BlockEntity extends EnergyCollectorMK1BlockEntity{
    public EnergyCollectorMK2BlockEntity() {
        super(Constants.COLLECTOR_MK2_MAX, Constants.COLLECTOR_MK2_GEN);
    }

    @Override
    protected int getInvSize() {
        return 12;
    }
}
