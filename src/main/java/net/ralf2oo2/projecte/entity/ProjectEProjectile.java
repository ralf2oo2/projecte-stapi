package net.ralf2oo2.projecte.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ralf2oo2.projecte.item.ProjectEItem;
import net.ralf2oo2.projecte.util.PlayerHelper;
import net.ralf2oo2.projecte.util.StackUtil;

import java.util.List;

public abstract class ProjectEProjectile extends Entity {
    private int blockX = -1;
    private int blockY = -1;
    private int blockZ = -1;
    private int blockId = 0;
    private boolean inGround = false;
    public int shake = 0;
    private LivingEntity owner;
    private int removalTimer;
    private int inAirTime = 0;

    public ProjectEProjectile(World world) {
        super(world);
        this.setBoundingBoxSpacing(0.25F, 0.25F);
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        double var3 = this.boundingBox.getAverageSideLength() * (double)4.0F;
        var3 *= 64.0F;
        return distance < var3 * var3;
    }

    public ProjectEProjectile(World world, LivingEntity owner) {
        super(world);
        this.owner = owner;
        this.setBoundingBoxSpacing(0.25F, 0.25F);
        this.setPositionAndAnglesKeepPrevAngles(owner.x, owner.y + (double)owner.getEyeHeight(), owner.z, owner.yaw, owner.pitch);
        this.x -= MathHelper.cos(this.yaw / 180.0F * (float)Math.PI) * 0.16F;
        this.y -= 0.1F;
        this.z -= MathHelper.sin(this.yaw / 180.0F * (float)Math.PI) * 0.16F;
        this.setPosition(this.x, this.y, this.z);
        this.standingEyeHeight = 0.0F;
        float var3 = 0.4F;
        this.velocityX = -MathHelper.sin(this.yaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float)Math.PI) * var3;
        this.velocityZ = MathHelper.cos(this.yaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float)Math.PI) * var3;
        this.velocityY = -MathHelper.sin(this.pitch / 180.0F * (float)Math.PI) * var3;
        this.setVelocity(this.velocityX, this.velocityY, this.velocityZ, 1.5F, 1.0F);
    }

    public ProjectEProjectile(World world, double x, double y, double z) {
        super(world);
        this.removalTimer = 0;
        this.setBoundingBoxSpacing(0.25F, 0.25F);
        this.setPosition(x, y, z);
        this.standingEyeHeight = 0.0F;
    }

    public void setVelocity(Entity entity, float pitch, float yaw, float pitchOffset, float speed, float divergence) {
        float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        float f1 = -MathHelper.sin((pitch + pitchOffset) * 0.017453292F);
        float f2 = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);

        this.setVelocity(f, f1, f2, speed, divergence);

        this.velocityX += entity.velocityX;
        this.velocityZ += entity.velocityZ;
        if (!entity.onGround) {
            this.velocityY += entity.velocityY;
        }
    }

    public void setVelocity(double x, double y, double z, float speed, float divergence) {
        float var9 = MathHelper.sqrt(x * x + y * y + z * z);
        x /= var9;
        y /= var9;
        z /= var9;
        x += this.random.nextGaussian() * (double)0.0075F * (double)divergence;
        y += this.random.nextGaussian() * (double)0.0075F * (double)divergence;
        z += this.random.nextGaussian() * (double)0.0075F * (double)divergence;
        x *= speed;
        y *= speed;
        z *= speed;
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
        float var10 = MathHelper.sqrt(x * x + z * z);
        this.prevYaw = this.yaw = (float)(Math.atan2(x, z) * (double)180.0F / (double)(float)Math.PI);
        this.prevPitch = this.pitch = (float)(Math.atan2(y, var10) * (double)180.0F / (double)(float)Math.PI);
        this.removalTimer = 0;
    }

    @Environment(EnvType.CLIENT)
    public void setVelocityClient(double x, double y, double z) {
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            float var7 = MathHelper.sqrt(x * x + z * z);
            this.prevYaw = this.yaw = (float)(Math.atan2(x, z) * (double)180.0F / (double)(float)Math.PI);
            this.prevPitch = this.pitch = (float)(Math.atan2(y, var7) * (double)180.0F / (double)(float)Math.PI);
        }

    }

    @Override
    public void tick() {
        this.lastTickX = this.x;
        this.lastTickY = this.y;
        this.lastTickZ = this.z;
        super.tick();
        if (this.shake > 0) {
            --this.shake;
        }

        if (this.inGround) {
            int blockId = this.world.getBlockId(this.blockX, this.blockY, this.blockZ);
            if (blockId == this.blockId) {
                ++this.removalTimer;
                if (this.removalTimer == 1200) {
                    this.markDead();
                }

                return;
            }

            this.inGround = false;
            this.velocityX *= this.random.nextFloat() * 0.2F;
            this.velocityY *= this.random.nextFloat() * 0.2F;
            this.velocityZ *= this.random.nextFloat() * 0.2F;
            this.removalTimer = 0;
            this.inAirTime = 0;
        } else {
            ++this.inAirTime;
        }

        Vec3d pos = Vec3d.createCached(this.x, this.y, this.z);
        Vec3d offsetPos = Vec3d.createCached(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
        HitResult hitResult = this.world.raycast(pos, offsetPos);
        pos = Vec3d.createCached(this.x, this.y, this.z);
        offsetPos = Vec3d.createCached(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
        if (hitResult != null) {
            offsetPos = Vec3d.createCached(hitResult.pos.x, hitResult.pos.y, hitResult.pos.z);
        }

        if (!this.world.isRemote) {
            Entity entity = null;
            List entities = this.world.getEntities(this, this.boundingBox.stretch(this.velocityX, this.velocityY, this.velocityZ).expand(1.0F, 1.0F, 1.0F));
            double totalDistance = 0.0F;

            for (Object o : entities) {
                Entity currentEntity = (Entity) o;
                if (currentEntity.isCollidable() && (currentEntity != this.owner || this.inAirTime >= 5)) {
                    Box bounds = currentEntity.boundingBox.expand(0.3F, 0.3F, 0.3F);
                    HitResult entityHit = bounds.raycast(pos, offsetPos);
                    if (entityHit != null) {
                        double distance = pos.distanceTo(entityHit.pos);
                        if (distance < totalDistance || totalDistance == (double) 0.0F) {
                            entity = currentEntity;
                            totalDistance = distance;
                        }
                    }
                }
            }

            if (entity != null) {
                hitResult = new HitResult(entity);
            }
        }

        if (hitResult != null) {
            this.onImpact(hitResult);
            if (hitResult.entity != null && hitResult.entity.damage(this.owner, 0)) {
            }

            this.markDead();
        }

        this.x += this.velocityX;
        this.y += this.velocityY;
        this.z += this.velocityZ;
        float var19 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
        this.yaw = (float)(Math.atan2(this.velocityX, this.velocityZ) * (double)180.0F / (double)(float)Math.PI);

        for(this.pitch = (float)(Math.atan2(this.velocityY, var19) * (double)180.0F / (double)(float)Math.PI); this.pitch - this.prevPitch < -180.0F; this.prevPitch -= 360.0F) {
        }

        while(this.pitch - this.prevPitch >= 180.0F) {
            this.prevPitch += 360.0F;
        }

        while(this.yaw - this.prevYaw < -180.0F) {
            this.prevYaw -= 360.0F;
        }

        while(this.yaw - this.prevYaw >= 180.0F) {
            this.prevYaw += 360.0F;
        }

        this.pitch = this.prevPitch + (this.pitch - this.prevPitch) * 0.2F;
        this.yaw = this.prevYaw + (this.yaw - this.prevYaw) * 0.2F;
        float var20 = 0.99F;
        float var21 = this.getGravity();
        if (this.isSubmergedInWater()) {
            for(int var7 = 0; var7 < 4; ++var7) {
                float var22 = 0.25F;
                this.world.addParticle("bubble", this.x - this.velocityX * (double)var22, this.y - this.velocityY * (double)var22, this.z - this.velocityZ * (double)var22, this.velocityX, this.velocityY, this.velocityZ);
            }

            var20 = 0.8F;
        }

        this.velocityX *= var20;
        this.velocityY *= var20;
        this.velocityZ *= var20;
        this.velocityY -= var21;
        this.setPosition(this.x, this.y, this.z);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putShort("xTile", (short)this.blockX);
        nbt.putShort("yTile", (short)this.blockY);
        nbt.putShort("zTile", (short)this.blockZ);
        nbt.putByte("inTile", (byte)this.blockId);
        nbt.putByte("shake", (byte)this.shake);
        nbt.putByte("inGround", (byte)(this.inGround ? 1 : 0));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.blockX = nbt.getShort("xTile");
        this.blockY = nbt.getShort("yTile");
        this.blockZ = nbt.getShort("zTile");
        this.blockId = nbt.getByte("inTile") & 255;
        this.shake = nbt.getByte("shake") & 255;
        this.inGround = nbt.getByte("inGround") == 1;
    }

    @Override
    public void onPlayerInteraction(PlayerEntity player) {
        if (this.inGround && this.owner == player && this.shake <= 0 && player.inventory.addStack(new ItemStack(Item.ARROW, 1))) {
            this.world.playSound(this, "random.pop", 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.sendPickup(this, 1);
            this.markDead();
        }

    }

    @Environment(EnvType.CLIENT)
    public float getShadowRadius() {
        return 0.0F;
    }

    public LivingEntity getOwner() {
        return owner;
    }

    public void onImpact(HitResult hit) {
        if(getOwner() instanceof PlayerEntity) {
            apply(hit);
        }
        if(!world.isRemote) {
            this.markDead();
        }
    }

    protected float getGravity() {
        return 0F;
    }

    protected abstract void apply(HitResult hit);

    protected final boolean tryConsumeEmc(ProjectEItem consumeFrom, long amount)
    {
        PlayerEntity player = ((PlayerEntity) getOwner());
        ItemStack found = PlayerHelper.findFirstItem(player, consumeFrom);
        return !StackUtil.isEmpty(found) && ProjectEItem.consumeFuel(player, found, amount, true);
    }

    @Override
    public String getTexture() {
        return "/assets/projecte/stationapi/textures/entity/fireball.png";
    }
}
