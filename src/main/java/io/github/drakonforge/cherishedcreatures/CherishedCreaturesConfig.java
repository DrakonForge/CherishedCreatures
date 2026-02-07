package io.github.drakonforge.cherishedcreatures;

import com.hypixel.hytale.codec.builder.BuilderCodec;

public class CherishedCreaturesConfig {
    public static final BuilderCodec<CherishedCreaturesConfig> CODEC = BuilderCodec.builder(
            CherishedCreaturesConfig.class, CherishedCreaturesConfig::new).build();
}
