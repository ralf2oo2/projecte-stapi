package net.ralf2oo2.projecte.api.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This interface specifies items that perform a specific function every tick when inside an activated Dark Matter Pedestal
 *
 * @author williewillus
 */
public interface PedestalItem {

    @Environment(EnvType.CLIENT)
    String TOOLTIPDISABLED = I18n.getTranslation("pe.pedestal.item_disabled");

    /***
     * Called on both client and server each time an active DMPedestalTile ticks with this item inside
     */
    void updateInPedestal(@NotNull World world, @NotNull BlockPos pos);

    /***
     * Called clientside when inside the pedestal gui to add special function descriptions
     * @return Brief strings describing the item's function in an activated pedestal
     */
    @Environment(EnvType.CLIENT)
    @NotNull List<String> getPedestalDescription();
}
