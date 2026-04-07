package net.chaosatom.thechaosengine.util.energy;

import java.text.DecimalFormat;

public class EnergyDisplayUtil {
    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
    
    public static String getFormattedEnergy(double fe) {
        if (fe >= 1_000_000) {
            return FORMAT.format(fe / 1_000_000) + " MFE";
        } else if (fe >= 1_000) {
            return FORMAT.format(fe / 1_000) + " kFE";
        } else {
            return (int) fe + " FE";
        }
    }
}
