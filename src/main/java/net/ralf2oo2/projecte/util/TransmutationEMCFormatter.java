package net.ralf2oo2.projecte.util;

import net.minecraft.client.resource.language.I18n;

public class TransmutationEMCFormatter {
    public static String EMCFormat(long EMC) {
        String postFix = "";
        double magnitude = 1;

        Double magnitudes[] = {1e12, 1e15, 1e18};

        for (int i=magnitudes.length - 1; i >= 0; i--) {
            double testMag = magnitudes[i];
            if (EMC >= testMag) {
                magnitude = testMag;
                postFix =  I18n.getTranslation("pe.emc.postfix." + i);
                break;
            }
        }

        return Constants.SINGLE_DP_EMC_FORMATTER.format(EMC / magnitude) + " " + postFix;
    }
}
