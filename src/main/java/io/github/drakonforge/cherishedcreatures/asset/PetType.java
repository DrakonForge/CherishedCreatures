package io.github.drakonforge.cherishedcreatures.asset;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.codec.AssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;

public class PetType implements JsonAssetWithMap<String, DefaultAssetMap<String, PetType>> {

    private static final AssetBuilderCodec.Builder<String, PetType> CODEC_BUILDER = AssetBuilderCodec.builder(
            PetType.class, PetType::new, Codec.STRING, (asset, id) -> asset.id = id, PetType::getId, (asset, data) -> asset.extraData = data, asset -> asset.extraData);
    public static final AssetCodec<String, PetType> CODEC = CODEC_BUILDER.build();
    private static AssetStore<String, PetType, DefaultAssetMap<String, PetType>> ASSET_STORE;

    public static final PetType DEFAULT = new PetType();

    public enum PetFeatureFlag {
        Bonding(false),
        FollowModeControls(true);

        private final boolean defaultValue;

        PetFeatureFlag(boolean defaultValue) {
            this.defaultValue = defaultValue;
        }

        public boolean getDefaultValue() {
            return defaultValue;
        }
    }

    public static AssetStore<String, PetType, DefaultAssetMap<String, PetType>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(PetType.class);
        }
        return ASSET_STORE;
    }

    public static void register() {
        if (AssetRegistry.getAssetStore(PetType.class) != null) {
            return;
        }
        AssetRegistry.register(
                HytaleAssetStore.builder(PetType.class, new DefaultAssetMap<>()).setPath("PetType").setCodec(CODEC).setKeyFunction(
                        PetType::getId).build());
    }

    protected String id;
    protected AssetExtraInfo.Data extraData;
    protected Object2BooleanMap<PetFeatureFlag> featureFlags;

    public boolean hasFeatureFlag(PetFeatureFlag flag) {
        return featureFlags.getOrDefault(flag, flag.getDefaultValue());
    }

    @Override
    public String getId() {
        return id;
    }
}
