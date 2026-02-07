package io.github.drakonforge.cherishedcreatures.asset;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.codec.AssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import io.github.drakonforge.cherishedcreatures.data.BondingActivity;
import io.github.drakonforge.cherishedcreatures.util.Object2BooleanMapCodec;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;

public class PetType implements JsonAssetWithMap<String, DefaultAssetMap<String, PetType>> {

    public static final PetType DEFAULT = new PetType();
    private static final AssetBuilderCodec.Builder<String, PetType> CODEC_BUILDER = AssetBuilderCodec.builder(
                    PetType.class, PetType::new, Codec.STRING, (asset, id) -> asset.id = id, PetType::getId,
                    (asset, data) -> asset.extraData = data, asset -> asset.extraData)
            .append(new KeyedCodec<>("BondingActivities", Codec.STRING_ARRAY),
                    (asset, bondingActivities) -> asset.bondingActivities = bondingActivities,
                    asset -> asset.bondingActivities)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("FeatureFlags", new Object2BooleanMapCodec<>(new EnumCodec<>(
                            PetFeatureFlag.class), Object2BooleanArrayMap::new)),
                    (asset, featureFlags) -> asset.featureFlags = featureFlags,
                    asset -> asset.featureFlags)
            .documentation("TODO")
            .add()
            .documentation("TODO");
    public static final AssetCodec<String, PetType> CODEC = CODEC_BUILDER.build();
    private static AssetStore<String, PetType, DefaultAssetMap<String, PetType>> ASSET_STORE;

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
        AssetRegistry.register(HytaleAssetStore.builder(PetType.class, new DefaultAssetMap<>())
                .setPath("PetType")
                .setCodec(CODEC)
                .setKeyFunction(PetType::getId)
                .build());
    }

    protected String id;
    protected AssetExtraInfo.Data extraData;
    protected Object2BooleanMap<PetFeatureFlag> featureFlags;
    // TODO: Optimize this out to a set or bit flag later
    protected String[] bondingActivities = { BondingActivity.ADVENTURING, BondingActivity.PETTING };

    public boolean hasFeatureFlag(PetFeatureFlag flag) {
        return featureFlags.getOrDefault(flag, flag.getDefaultValue());
    }

    public boolean hasBondingActivity(String activityName) {
        for (int i = 0; i < this.bondingActivities.length; i++) {
            if (this.bondingActivities[i].equals(activityName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getId() {
        return id;
    }

    public enum PetFeatureFlag {
        Bonding(false), FollowModeControls(true);

        private final boolean defaultValue;

        PetFeatureFlag(boolean defaultValue) {
            this.defaultValue = defaultValue;
        }

        public boolean getDefaultValue() {
            return defaultValue;
        }
    }
}
