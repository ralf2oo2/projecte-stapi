package net.ralf2oo2.projecte.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.ralf2oo2.projecte.item.ProjectEItem;

public class PlayerHelper {
    public static ItemStack findFirstItem(PlayerEntity player, ProjectEItem consumeFrom)
    {
        for (ItemStack s : player.inventory.main)
        {
            if (!StackUtil.isEmpty(s) && s.getItem() == consumeFrom)
            {
                return s;
            }
        }
        return null;
    }

    public static Box getBoundingBox(PlayerEntity player) {
        return Box.create(player.x - player.width / 2, player.y - player.height / 2, player.z - player.width / 2,
                player.x + player.width / 2, player.y + player.height / 2, player.z + player.height / 2);
    }
}
