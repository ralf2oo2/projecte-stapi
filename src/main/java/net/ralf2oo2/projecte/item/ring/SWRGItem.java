package net.ralf2oo2.projecte.item.ring;

import net.danygames2014.nyalib.abilities.ability.Ability;
import net.danygames2014.nyalib.abilities.ability.impl.Abilities;
import net.danygames2014.nyalib.abilities.ability.impl.inventoryprovider.InventoryAbilityItem;
import net.danygames2014.nyalib.abilities.ability.impl.inventoryprovider.InventoryAbilityItemSlot;
import net.danygames2014.nyalib.abilities.ability.value.AbilityValue;
import net.danygames2014.nyalib.abilities.ability.value.BooleanAbilityValue;
import net.danygames2014.nyalib.sound.SoundHelper;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Formatting;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.api.item.PedestalItem;
import net.ralf2oo2.projecte.api.item.ProjectileShooter;
import net.ralf2oo2.projecte.block.entity.DarkMatterPedestalBlockEntity;
import net.ralf2oo2.projecte.config.Config;
import net.ralf2oo2.projecte.entity.SWRGProjectileEntity;
import net.ralf2oo2.projecte.item.FlightProvider;
import net.ralf2oo2.projecte.item.ProjectEItem;
import net.ralf2oo2.projecte.util.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SWRGItem extends ProjectEItem implements PedestalItem, ProjectileShooter, InventoryAbilityItem {
    public SWRGItem(Identifier identifier) {
        super(identifier);
        this.setMaxCount(1);
    }

    private void tick(ItemStack stack, PlayerEntity player) {
        if (stack.getStationNbt().getInt(TAG_MODE) > 1) {
            // Repel on both sides - smooth animation
            WorldHelper.repelEntitiesInAABBFromPoint(player.world, PlayerHelper.getBoundingBox(player).expand(5, 5, 5), player.x, player.y, player.z, true);
        }

        if (player.world.isRemote) {
            return;
        }

        if (getEmc(stack) == 0 && !consumeFuel(player, stack, 64, false))
        {
            if (stack.getStationNbt().getInt(TAG_MODE) > 0)
            {
                changeMode(player, stack, 0);
            }

            if (stack.getStationNbt().getBoolean("flight"))
            {
                stack.getStationNbt().putBoolean("flight", false);
            }

            return;
        }

        if (!stack.getStationNbt().getBoolean("flight"))
        {
            stack.getStationNbt().putBoolean("flight", true);
        }

        if (player.nyalib$isFlying())
        {
            if (!isFlyingEnabled(stack))
            {
                changeMode(player, stack, stack.getStationNbt().getInt(TAG_MODE) == 0 ? 1 : 3);
            }
        }
        else
        {
            if (isFlyingEnabled(stack))
            {
                changeMode(player, stack, stack.getStationNbt().getInt(TAG_MODE) == 1 ? 0 : 2);
            }
        }

        float toRemove = 0;

        if (player.nyalib$isFlying())
        {
            toRemove = 0.32F;
        }

        if (stack.getStationNbt().getInt(TAG_MODE) == 2)
        {
            toRemove = 0.32F;
        }
        else if (stack.getStationNbt().getInt(TAG_MODE) == 3)
        {
            toRemove = 0.64F;
        }

        removeEmc(stack, EMCHelper.removeFractionalEMC(stack, toRemove));
    }

    private boolean isFlyingEnabled(ItemStack stack)
    {
        return stack.getStationNbt().getInt(TAG_MODE) == 1 || stack.getStationNbt().getInt(TAG_MODE)== 3;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (slot > 8 || !(entity instanceof PlayerEntity))
        {
            return;
        }
        tick(stack, (PlayerEntity) entity);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, PlayerEntity user) {
        if(!world.isRemote) {
            int newMode = switch (stack.getStationNbt().getInt(TAG_MODE)) {
                case 0 -> 2;
                case 1 -> 3;
                case 2 -> 0;
                case 3 -> 1;
                default -> 0;
            };
            changeMode(user, stack, newMode);
        }
        return stack;
    }

    /**
     * Change the mode of SWRG. Modes:<p>
     * 0 = Ring Off<p>
     * 1 = Flight<p>
     * 2 = Shield<p>
     * 3 = Flight + Shield<p>
     */
    public void changeMode(PlayerEntity player, ItemStack stack, int mode)
    {
        int oldMode = stack.getStationNbt().getInt(TAG_MODE);
        if (mode == oldMode)
        {
            return;
        }
        stack.getStationNbt().putInt(TAG_MODE, mode);
        if (player == null)
        {
            //Don't do sounds if the player is null
            return;
        }
        if (mode == 0 || oldMode == 3)
        {
            //At least one mode deactivated
            SoundHelper.playSound(player.world, player.x, player.y, player.z, Sounds.UNCHARGE, 0.8F, 1.0F);
        }
        else if (oldMode == 0 || mode == 3)
        {
            //At least one mode activated
            SoundHelper.playSound(player.world, player.x, player.y, player.z, Sounds.HEAL, 0.8F, 1.0F);
        }
        //Doesn't handle going from mode 1 to 2 or 2 to 1
    }

    @Override
    public void updateInPedestal(@NotNull World world, @NotNull BlockPos pos) {
        if (!world.isRemote && Config.PEDESTAL_CONFIG.swrgPedCooldown != -1)
        {
            BlockEntity blockEntity = world.getBlockEntity(pos.getX(), pos.getY(), pos.getZ());
            if(!(blockEntity instanceof DarkMatterPedestalBlockEntity pedestal))
            {
                return;
            }
            if (pedestal.getActivityCooldown() <= 0)
            {
                List<Object> list = world.collectEntitiesByClass(LivingEntity.class, pedestal.getEffectBounds());
                for (Object livingObj : list)
                {
                    if (livingObj instanceof WolfEntity && ((WolfEntity) livingObj).isTamed() || livingObj instanceof PlayerEntity)
                    {
                        continue;
                    }
                    LivingEntity living = (LivingEntity) livingObj;
                    world.spawnEntity(new LightningEntity(world, living.x, living.y, living.z));
                }
                pedestal.setActivityCooldown(Config.PEDESTAL_CONFIG.swrgPedCooldown);
            }
            else
            {
                pedestal.decrementActivityCooldown();
            }
        }
    }

    @Override
    public @NotNull List<String> getPedestalDescription() {
        List<String> list = new ArrayList<>();
        if (Config.PEDESTAL_CONFIG.swrgPedCooldown != -1)
        {
            list.add(Formatting.BLUE + I18n.getTranslation("pe.swrg.pedestal1"));
            list.add(Formatting.BLUE + I18n.getTranslation("pe.swrg.pedestal2", MathUtils.tickToSecFormatted(Config.PEDESTAL_CONFIG.swrgPedCooldown)));
        }
        return list;
    }

    @Override
    public boolean shootProjectile(@NotNull PlayerEntity player, @NotNull ItemStack stack) {
        SWRGProjectileEntity projectile = new SWRGProjectileEntity(player.world, player, false);
        projectile.setVelocity(player, player.pitch, player.yaw, 0, 1.5F, 1);
        player.world.spawnEntity(projectile);
        return true;
    }

    @Override
    public Ability<?, ?>[] getProvidedAbilities(PlayerEntity player, InventoryAbilityItemSlot slotType) {
        if(slotType != InventoryAbilityItemSlot.HOTBAR) {
            return new Ability[0];
        }
        return new Ability[] {
                Abilities.FLIGHT
        };
    }

    @Override
    public AbilityValue<?> getAbilityValue(Ability<?, ?> ability, PlayerEntity player, PlayerInventory playerInventory, ItemStack stack, InventoryAbilityItemSlot slotType, int slot) {
        if (ability == Abilities.FLIGHT && slotType == InventoryAbilityItemSlot.HOTBAR && stack.getStationNbt().getBoolean("flight")) {
            return BooleanAbilityValue.of(true);
        }

        return BooleanAbilityValue.of(false);
    }
}
