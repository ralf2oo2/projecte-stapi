package net.ralf2oo2.projecte.item;

import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Formatting;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.api.item.AlchemicalBagItem;
import net.ralf2oo2.projecte.api.item.AlchemicalChestItem;
import net.ralf2oo2.projecte.api.item.ModeChanger;
import net.ralf2oo2.projecte.api.item.PedestalItem;
import net.ralf2oo2.projecte.block.entity.AlchemicalChestBlockEntity;
import net.ralf2oo2.projecte.block.entity.DarkMatterPedestalBlockEntity;
import net.ralf2oo2.projecte.capability.InternalTimersEntityCapability;
import net.ralf2oo2.projecte.config.Config;
import net.ralf2oo2.projecte.item.ring.ToggleRingItem;
import net.ralf2oo2.projecte.util.ItemHelper;
import net.ralf2oo2.projecte.util.MathUtils;
import net.ralf2oo2.projecte.util.StackUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RepairTalismanItem extends ProjectEItem implements AlchemicalBagItem, AlchemicalChestItem, PedestalItem {
    public RepairTalismanItem(Identifier identifier) {
        super(identifier);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(world.isRemote || !(entity instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) entity;

        InternalTimersEntityCapability capability = CapabilityHelper.getCapability(player, InternalTimersEntityCapability.class);
        if(capability != null) {
            capability.activateRepair();

            if(capability.canRepair()) {
                repairAllItems(player);
            }
        }
    }

    private void repairAllItems(PlayerEntity player)
    {
        Inventory inv = player.inventory;

        for (int i = 0; i < inv.size(); i++)
        {
            ItemStack invStack = inv.getStack(i);

            if (StackUtil.isEmpty(invStack) || invStack.getItem() instanceof ModeChanger || !invStack.getItem().isDamageable())
            {
                continue;
            }

            if (invStack == player.getHand() && player.swingAnimationProgress > 0)
            {
                //Don't repair item that is currently used by the player.
                continue;
            }

            if (ItemHelper.isDamageable(invStack) && invStack.getDamage() > 0)
            {
                invStack.setDamage(invStack.getDamage() - 1);
            }
        }
    }

    @Override
    public void updateInPedestal(@NotNull World world, @NotNull BlockPos pos) {
        if (!world.isRemote && Config.PEDESTAL_CONFIG.repairPedCooldown != -1)
        {
            DarkMatterPedestalBlockEntity blockEntity = (DarkMatterPedestalBlockEntity)world.getBlockEntity(pos.getX(), pos.getY(), pos.getZ());
            if (blockEntity.getActivityCooldown() == 0)
            {
                world.collectEntitiesByClass(PlayerEntity.class, blockEntity.getEffectBounds()).forEach(player -> repairAllItems((PlayerEntity) player));
                blockEntity.setActivityCooldown(Config.PEDESTAL_CONFIG.repairPedCooldown);
            }
            else
            {
                blockEntity.decrementActivityCooldown();
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public @NotNull List<String> getPedestalDescription() {
        List<String> list = new ArrayList<>();
        if (Config.PEDESTAL_CONFIG.repairPedCooldown != -1)
        {
            list.add(Formatting.BLUE + I18n.getTranslation("pe.repairtalisman.pedestal1"));
            list.add(Formatting.BLUE + I18n.getTranslation("pe.repairtalisman.pedestal2", MathUtils.tickToSecFormatted(Config.PEDESTAL_CONFIG.repairPedCooldown)));
        }
        return list;
    }

    @Override
    public void updateInAlchChest(@NotNull World world, int x, int y, int z, @NotNull ItemStack stack) {
        if (world.isRemote)
        {
            return;
        }

        BlockEntity te = world.getBlockEntity(x, y, z);
        if (!(te instanceof AlchemicalChestBlockEntity tile))
        {
            return;
        }

        byte coolDown = stack.getStationNbt().getByte("Cooldown");

        if (coolDown > 0)
        {
            stack.getStationNbt().putByte("Cooldown", (byte) (coolDown - 1));
        }
        else
        {
            boolean hasAction = false;

            Inventory inv = tile;
            for (int i = 0; i < inv.size(); i++)
            {
                ItemStack invStack = inv.getStack(i);

                if (StackUtil.isEmpty(invStack) || invStack.getItem() instanceof ToggleRingItem || !invStack.getItem().isDamageable())
                {
                    continue;
                }

                if (ItemHelper.isDamageable(invStack) && invStack.getDamage() > 0)
                {
                    invStack.setDamage(invStack.getDamage() - 1);

                    if (!hasAction)
                    {
                        hasAction = true;
                    }
                }
            }

            if (hasAction)
            {
                stack.getStationNbt().putByte("Cooldown", (byte) 19);
                tile.markDirty();
            }
        }
    }

    @Override
    public boolean updateInAlchBag(@NotNull Inventory inv, @NotNull PlayerEntity player, @NotNull ItemStack stack) {
        if (player.world.isRemote)
        {
            return false;
        }

        byte coolDown = stack.getStationNbt().getByte("Cooldown");

        if (coolDown > 0)
        {
            stack.getStationNbt().putByte("Cooldown", (byte) (coolDown - 1));
        }
        else
        {
            boolean hasAction = false;

            for (int i = 0; i < inv.size(); i++)
            {
                ItemStack invStack = inv.getStack(i);

                if (StackUtil.isEmpty(invStack) || invStack.getItem() instanceof ToggleRingItem || !invStack.getItem().isDamageable())
                {
                    continue;
                }

                if (ItemHelper.isDamageable(invStack) && invStack.getDamage() > 0)
                {
                    invStack.setDamage(invStack.getDamage() - 1);

                    if (!hasAction)
                    {
                        hasAction = true;
                    }
                }
            }

            if (hasAction)
            {
                stack.getStationNbt().putByte("Cooldown", (byte) 19);
                return true;
            }
        }
        return false;
    }
}
