package io.github.drakonforge.cherishedcreatures.util;

public class BondingHelpers {
    public static int getBondingLevel(float[] bondingLevelValues, float bondingXp) {
        for (int level = 0; level < bondingLevelValues.length; ++level) {
            if (bondingXp < bondingLevelValues[level]) {
                return level;
            }
        }
        // Max level
        return bondingLevelValues.length;
    }

    public static int getXpToNextLevel(float[] bondingLevelValues) {
        return -1;
    }
}
