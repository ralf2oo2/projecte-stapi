package net.ralf2oo2.projecte.listener;

import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.event.gui.screen.container.TooltipBuildEvent;
import net.modificationstation.stationapi.api.util.Formatting;
import net.ralf2oo2.projecte.api.capability.KnowledgeEntityCapability;
import net.ralf2oo2.projecte.api.item.ItemEmc;
import net.ralf2oo2.projecte.api.item.PedestalItem;
import net.ralf2oo2.projecte.config.Config;
import net.ralf2oo2.projecte.util.Constants;
import net.ralf2oo2.projecte.util.EMCHelper;
import org.lwjgl.input.Keyboard;

import java.math.BigInteger;
import java.util.List;

public class TooltipListener {
    @EventListener
    public void onTooltipBuild(TooltipBuildEvent event) {
        ItemStack current = event.itemStack;
        if(current == null) {
            return;
        }
        Item currentItem = current.getItem();
        Block currentBlock = currentItem instanceof BlockItem ? ((BlockItem)currentItem).getBlock() : null;
        PlayerEntity playerEntity = Minecraft.INSTANCE.player;

        if(Config.MISCELANIOUS_CONFIG.pedestalToolTips && currentItem instanceof PedestalItem pedestalItem) {
            List<String> description = pedestalItem.getPedestalDescription();
            if(description.isEmpty()) {
                event.tooltip.add(PedestalItem.TOOLTIPDISABLED);
            } else {
                event.tooltip.addAll(description);
            }
        }

        if(Config.MISCELANIOUS_CONFIG.emcToolTips) {
            if (EMCHelper.doesItemHaveEmc(current))
            {
                long value = EMCHelper.getEmcValue(current);

                event.tooltip.add(Formatting.YELLOW +
                                               I18n.getTranslation("pe.emc.emc_tooltip_prefix") + " " + Formatting.WHITE + Constants.EMC_FORMATTER.format(value) + Formatting.BLUE + EMCHelper.getEmcSellString(current, 1));

                if (current.count > 1)
                {
                    event.tooltip.add(Formatting.YELLOW + I18n.getTranslation("pe.emc.stackemc_tooltip_prefix") + " " +
                                              Formatting.WHITE + Constants.EMC_FORMATTER.format(BigInteger.valueOf(value).multiply(BigInteger.valueOf(current.count))) +
                                              Formatting.BLUE + EMCHelper.getEmcSellString(current, current.count));
                }

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && playerEntity != null)
                {
                    KnowledgeEntityCapability capability = CapabilityHelper.getCapability(playerEntity, KnowledgeEntityCapability.class);
                    if(capability != null && capability.hasKnowledge(current)) {
                        event.tooltip.add(Formatting.YELLOW + I18n.getTranslation("pe.emc.has_knowledge"));
                    }
                }
            }
        }

        if(Config.MISCELANIOUS_CONFIG.statToolTips) {
            /*
             * Collector ToolTips
             */
            String unit = I18n.getTranslation("pe.emc.name");
            String rate = I18n.getTranslation("pe.emc.rate");

            if (currentBlock == BlockListener.collectorMK1 && false)
            {
                event.tooltip.add(Formatting.DARK_PURPLE
                                               + String.format(I18n.getTranslation("pe.emc.maxgenrate_tooltip")
                                                                       + Formatting.BLUE + " %d " + rate, Constants.COLLECTOR_MK1_GEN));
                event.tooltip.add(Formatting.DARK_PURPLE
                                               + String.format(I18n.getTranslation("pe.emc.maxstorage_tooltip")
                                                                       + Formatting.BLUE + " %d " + unit, Constants.COLLECTOR_MK1_MAX));
            }

            if (currentBlock == BlockListener.collectorMK2 && false)
            {
                event.tooltip.add(Formatting.DARK_PURPLE
                                               + String.format(I18n.getTranslation("pe.emc.maxgenrate_tooltip")
                                                                       + Formatting.BLUE + " %d " + rate, Constants.COLLECTOR_MK2_GEN));
                event.tooltip.add(Formatting.DARK_PURPLE
                                               + String.format(I18n.getTranslation("pe.emc.maxstorage_tooltip")
                                                                       + Formatting.BLUE + " %d " + unit, Constants.COLLECTOR_MK2_MAX));
            }

            if (currentBlock == BlockListener.collectorMK3 && false)
            {
                event.tooltip.add(Formatting.DARK_PURPLE
                                               + String.format(I18n.getTranslation("pe.emc.maxgenrate_tooltip")
                                                                       + Formatting.BLUE + " %d " + rate, Constants.COLLECTOR_MK3_GEN));
                event.tooltip.add(Formatting.DARK_PURPLE
                                               + String.format(I18n.getTranslation("pe.emc.maxstorage_tooltip")
                                                                       + Formatting.BLUE + " %d " + unit, Constants.COLLECTOR_MK3_MAX));
            }

            /*
             * Relay ToolTips
             */
            if (currentBlock == BlockListener.relay && false)
            {
                event.tooltip.add(Formatting.DARK_PURPLE
                                               + String.format(I18n.getTranslation("pe.emc.maxoutrate_tooltip")
                                                                       + Formatting.BLUE + " %d " + rate, Constants.RELAY_MK1_OUTPUT));
                event.tooltip.add(Formatting.DARK_PURPLE
                                               + String.format(I18n.getTranslation("pe.emc.maxstorage_tooltip")
                                                                       + Formatting.BLUE + " %d " + unit, Constants.RELAY_MK1_MAX));
            }

            if (currentBlock == BlockListener.relayMK2 && false)
            {
                event.tooltip.add(Formatting.DARK_PURPLE
                                               + String.format(I18n.getTranslation("pe.emc.maxoutrate_tooltip")
                                                                       + Formatting.BLUE + " %d " + rate, Constants.RELAY_MK2_OUTPUT));
                event.tooltip.add(Formatting.DARK_PURPLE
                                               + String.format(I18n.getTranslation("pe.emc.maxstorage_tooltip")
                                                                       + Formatting.BLUE + " %d " + unit, Constants.RELAY_MK2_MAX));
            }

            if (currentBlock == BlockListener.relayMK3 && false)
            {
                event.tooltip.add(Formatting.DARK_PURPLE
                                               + String.format(I18n.getTranslation("pe.emc.maxoutrate_tooltip")
                                                                       + Formatting.BLUE + " %d " + rate, Constants.RELAY_MK3_OUTPUT));
                event.tooltip.add(Formatting.DARK_PURPLE
                                               + String.format(I18n.getTranslation("pe.emc.maxstorage_tooltip")
                                                                       + Formatting.BLUE + " %d " + unit, Constants.RELAY_MK3_MAX));
            }
        }

        if(currentItem instanceof ItemEmc || current.getStationNbt().contains("StoredEMC")) {
            long value;
            if (current.getStationNbt().contains("StoredEMC"))
            {
                value = current.getStationNbt().getLong("StoredEMC");
            } else
            {
                value = ((ItemEmc) current.getItem()).getStoredEmc(current);
            }
            event.tooltip.add(Formatting.YELLOW + I18n.getTranslation("pe.emc.storedemc_tooltip") + " " + Formatting.WHITE + Constants.EMC_FORMATTER.format(value));
        }
    }
}
