package net.ralf2oo2.projecte.item;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.modificationstation.stationapi.api.util.Formatting;
import net.modificationstation.stationapi.api.util.Identifier;
import net.ralf2oo2.projecte.api.item.ItemCharge;
import net.ralf2oo2.projecte.api.item.ItemWithDisplayDurability;
import net.ralf2oo2.projecte.api.item.ModeChanger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ModeItem extends ProjectEItem implements ModeChanger, ItemCharge, ItemWithDisplayDurability, CustomTooltipProvider {
    private final byte numModes;
    private final int numCharge;
    private final String[] modes;

    public ModeItem(Identifier identifier, int numCharge, String[] modes) {
        super(identifier);
        this.setMaxCount(1);
        this.numModes = (byte)modes.length;
        this.numCharge = numCharge;
        this.modes = modes;
    }

    @Override
    public byte getMode(@NotNull ItemStack stack) {
        return stack.getStationNbt().getByte(TAG_MODE);
    }

    private String getUnlocalizedMode(ItemStack stack) {
        return modes[stack.getStationNbt().getByte(TAG_MODE)];
    }

    protected void changeMode(ItemStack stack) {
        byte newMode = (byte) (getMode(stack) + 1);
        stack.getStationNbt().putByte(TAG_MODE, (newMode > numModes - 1 ? 0 : newMode));
    }

    @Override
    public boolean changeMode(@NotNull PlayerEntity player, @NotNull ItemStack stack) {
        if (numModes == 0) {
            return false;
        }
        changeMode(stack);

        String modeName = I18n.getTranslation(modes[getMode(stack)]);
        player.sendMessage(String.format(I18n.getTranslation("pe.item.mode_switch"), modeName));
        return true;
    }

    @Override
    public @NotNull String[] getTooltip(ItemStack stack, String originalTooltip) {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(originalTooltip);

        if(this.numModes > 0) {
            tooltip.add(I18n.getTranslation("pe.item.mode") + ": " + Formatting.AQUA + I18n.getTranslation(getUnlocalizedMode(stack)));
        }

        return tooltip.toArray(new String[0]);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0D - (double) getCharge(stack) / numCharge;
    }

    @Override
    public int getNumCharges(@NotNull ItemStack stack) {
        return numCharge;
    }
}
