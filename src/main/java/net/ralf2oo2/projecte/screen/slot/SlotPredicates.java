package net.ralf2oo2.projecte.screen.slot;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmeltingRecipeManager;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.recipe.FuelRegistry;
import net.ralf2oo2.projecte.api.item.ItemEmc;
import net.ralf2oo2.projecte.emc.FuelMapper;
import net.ralf2oo2.projecte.util.EMCHelper;
import net.ralf2oo2.projecte.util.ItemHelper;
import net.ralf2oo2.projecte.util.StackUtil;

import java.util.function.Predicate;

public final class SlotPredicates {
    public static final Predicate<ItemStack> HAS_EMC = input -> !StackUtil.isEmpty(input) && EMCHelper.doesItemHaveEmc(input);

    public static final Predicate<ItemStack> COLLECTOR_LOCK = input -> !StackUtil.isEmpty(input) && FuelMapper.isStackFuel(input);

    public static final Predicate<ItemStack> COLLECTOR_INV = input -> !StackUtil.isEmpty(input) && input.getItem() instanceof ItemEmc || (FuelMapper.isStackFuel(input) && !FuelMapper.isStackMaxFuel(input));

    // slotrelayklein, slotmercurialklein
    public static final Predicate<ItemStack> IITEMEMC = input -> !StackUtil.isEmpty(input) && input.getItem() instanceof ItemEmc;

    // slotrelayinput
    public static final Predicate<ItemStack> RELAY_INV = input -> IITEMEMC.test(input) || HAS_EMC.test(input);

    public static final Predicate<ItemStack> FURNACE_FUEL = input -> IITEMEMC.test(input) || !StackUtil.isEmpty(input) && FuelRegistry.getFuelTime(input) > 0;

    public static final Predicate<ItemStack> SMELTABLE = input -> !StackUtil.isEmpty(input) && !StackUtil.isEmpty(SmeltingRecipeManager.getInstance().craft(input.itemId));

    public static final Predicate<ItemStack> MERCURIAL_TARGET = input -> {
        if (StackUtil.isEmpty(input)) return false;
        BlockState state = ItemHelper.stackToState(input);
        return state != null && !(Block.BLOCKS_WITH_ENTITY[state.getBlock().id]) && EMCHelper.doesItemHaveEmc(input);
    };

    private SlotPredicates() {}
}
