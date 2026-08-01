package net.ralf2oo2.projecte.block.entity;

public class DarkMatterFurnaceBlockEntity extends RedMatterFurnaceBlockEntity {
    public DarkMatterFurnaceBlockEntity() {
        super(10, 3);
    }

    @Override
    protected int getInvSize() {
        return 9;
    }

    @Override
    protected float getOreDoubleChance() {
        return 0.5F;
    }

    @Override
    public int getCookProgressScaled(int value) {
        return furnaceCookTime * value / ticksBeforeSmelt;
    }
}
