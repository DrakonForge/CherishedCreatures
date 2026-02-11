package io.github.drakonforge.cherishedcreatures;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class CherishedCreaturesConfig {
    public static final BuilderCodec<CherishedCreaturesConfig> CODEC = BuilderCodec.builder(
            CherishedCreaturesConfig.class, CherishedCreaturesConfig::new).append(new KeyedCodec<>("DefaultBondingLevelValues", Codec.FLOAT_ARRAY), (config, value) -> config.defaultBondingLevelValues = value, CherishedCreaturesConfig::getDefaultBondingLevelValues).add().build();

    public static CherishedCreaturesConfig get() {
        return CherishedCreaturesPlugin.get().getConfig().get();
    }

    private float[] defaultBondingLevelValues = { 125.0f, 250.0f, 375.0f, 500.0f };

    public float[] getDefaultBondingLevelValues() {
        return defaultBondingLevelValues;
    }
}
