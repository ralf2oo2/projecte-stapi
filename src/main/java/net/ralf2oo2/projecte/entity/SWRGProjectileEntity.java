package net.ralf2oo2.projecte.entity;

import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ralf2oo2.projecte.item.ProjectEItem;
import net.ralf2oo2.projecte.listener.ItemListener;
import net.ralf2oo2.projecte.util.LivingEntityHelper;

public class SWRGProjectileEntity extends ProjectEProjectile{

    private boolean fromArcana = false;

    public SWRGProjectileEntity(World world) {
        super(world);
    }

    public SWRGProjectileEntity(World world, PlayerEntity player, boolean fromArcana) {
        super(world, player);
        this.fromArcana = fromArcana;
    }

    @Override
    public void tick() {
        super.tick();

        if (!world.isRemote && age > 400)
        {
            markDead();
            return;
        }

        // Undo the 0.99 (0.8 in water) drag applied in superclass
        double inverse = 1D / (isSubmergedInWater() ? 0.8D : 0.99D);
        velocityX *= inverse;
        velocityY *= inverse;
        velocityZ *= inverse;

        if (!world.isRemote && !dead && y > world.getHeight() && world.isRaining())
        {
            world.getProperties().setThundering(true);
            markDead();
        }
    }

    @Override
    protected void apply(HitResult hit) {
        if (world.isRemote)
        {
            return;
        }

        ProjectEItem consumeFrom = (ProjectEItem) (fromArcana ? ItemListener.arcana : ItemListener.swrg);

        switch (hit.type)
        {
            case BLOCK:
            {
                if(tryConsumeEmc(consumeFrom, 768))
                {
                    BlockPos pos = new BlockPos(hit.blockX, hit.blockY, hit.blockZ);

                    LightningEntity lightning = new LightningEntity(world, pos.getX(), pos.getY(), pos.getZ());
                    world.spawnEntity(lightning);

                    if (world.isThundering())
                    {
                        for (int i = 0; i < 3; i++)
                        {
                            LightningEntity bonus = new LightningEntity(world, pos.getX() + world.random.nextGaussian(), pos.getY() + world.random.nextGaussian(), pos.getZ() + world.random.nextGaussian());
                            world.spawnEntity(bonus);
                        }
                    }
                }

                break;
            }
            case ENTITY:
            {
                if (hit.entity instanceof LivingEntity && tryConsumeEmc(consumeFrom, 64))
                {
                    PlayerEntity player = (PlayerEntity) getOwner();

                    // Minor damage so we count as the attacker for launching the mob
                    hit.entity.damage(player, 1);

                    // Fake onGround before knockBack so you can re-launch mobs that have already been launched
                    boolean oldOnGround = hit.entity.onGround;
                    hit.entity.onGround = true;
                    LivingEntityHelper.applyKnockback((LivingEntity) hit.entity, 5, -velocityX * 0.25, -velocityZ * 0.25);
                    hit.entity.onGround = oldOnGround;
                    hit.entity.velocityY *= 3;
                }

                break;
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        fromArcana = nbt.getBoolean("fromArcana");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putBoolean("fromArcana", fromArcana);
    }

    @Override
    public String getTexture() {
        return "/assets/projecte/stationapi/textures/entity/lightning.png";
    }
}
