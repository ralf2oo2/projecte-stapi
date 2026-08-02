package net.ralf2oo2.projecte.recipe;

import net.minecraft.block.Block;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.tag.TagKey;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.util.EMCHelper;
import net.ralf2oo2.projecte.util.StackUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CovalenceRepairRecipe implements CraftingRecipe {
    @Override
    public boolean matches(CraftingInventory craftingInventory) {
        RepairTargetInfo targetInfo = findIngredients(craftingInventory);
        return targetInfo != null && targetInfo.emcPerDurability <= targetInfo.dustEmc;
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        RepairTargetInfo targetInfo = findIngredients(craftingInventory);
        if (targetInfo == null) {
            //If there isn't actually a match return no result
            return null;
        }
        ItemStack output = targetInfo.tool.copy();
        output.setDamage((int) Math.max(output.getDamage() - targetInfo.dustEmc / targetInfo.emcPerDurability, 0));
        return output;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(Block.STONE);
    }

    @Nullable
    private RepairTargetInfo findIngredients(CraftingInventory inv) {
        List<ItemStack> dust = new ArrayList<>();
        ItemStack tool = null;
        for (int i = 0; i < inv.size(); i++) {
            ItemStack input = inv.getStack(i);
            if (!StackUtil.isEmpty(input)) {
                if (input.getItem().getRegistryEntry().isIn(TagKey.of(ItemRegistry.KEY, Identifier.of("projecte:covalence_dusts")))) {
                    dust.add(input);
                } else if (StackUtil.isEmpty(tool) && input.isDamageable() && input.getDamage() > 0) {
                    tool = input;
                } else {//Invalid item
                    return null;
                }
            }
        }
        if (StackUtil.isEmpty(tool) || dust.isEmpty()) {
            //If there is no tool, or no dusts where found, return that we don't have any matching ingredients
            return null;
        }
        return new RepairTargetInfo(tool, dust.stream().mapToLong(EMCHelper::getEmcValue).sum());
    }

    private static class RepairTargetInfo {

        private final ItemStack tool;
        private final long emcPerDurability;
        private final long dustEmc;

        public RepairTargetInfo(ItemStack tool, long dustEmc) {
            this.tool = tool;
            this.dustEmc = dustEmc;
            this.emcPerDurability = EMCHelper.getEMCPerDurability(tool);
        }
    }
}
