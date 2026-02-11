package io.github.drakonforge.cherishedcreatures.util;

public class BondingHelpers {
    public static final int DEFAULT_NUM_SEGMENTS = 4;

    public static int getBondingLevel(float[] bondingLevelValues, float bondingXp) {
        for (int level = 0; level < bondingLevelValues.length; ++level) {
            if (bondingXp < bondingLevelValues[level]) {
                return level;
            }
        }
        // Max level
        return bondingLevelValues.length;
    }

    // Assume segments of equal width
    public static float getSegmentedBondingProgress(float[] bondingLevelValues, float bondingXp) {
        int numSegments = bondingLevelValues.length;
        for (int segmentsFilled = 0; segmentsFilled < numSegments; ++segmentsFilled) {
            float xpRequiredForNextLevel = bondingLevelValues[segmentsFilled];
            if (bondingXp < xpRequiredForNextLevel) {
                float xpRequiredForCurrentLevel;
                if (segmentsFilled > 0) {
                    xpRequiredForCurrentLevel = bondingLevelValues[segmentsFilled - 1];
                } else {
                    xpRequiredForCurrentLevel = 0.0f;
                }
                float progressToNextLevel = (bondingXp - xpRequiredForCurrentLevel) / (xpRequiredForNextLevel - xpRequiredForCurrentLevel);
                float levelFillProgress = (segmentsFilled + progressToNextLevel) / numSegments;
                return Math.clamp(levelFillProgress, 0.0f, 1.0f);
            }
        }
        return 1.0f;
    }

    public static float getLinearBondingProgress(float[] bondingLevelValues, float bondingXp) {
        float maxBondingXp = bondingLevelValues[bondingLevelValues.length - 1];
        if (maxBondingXp == 0.0f) {
            return 0.0f;
        }
        return bondingXp / maxBondingXp;
    }
}
