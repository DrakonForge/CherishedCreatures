package io.github.drakonforge.cherishedcreatures.asset;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.codec.AssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesConfig;
import io.github.drakonforge.cherishedcreatures.util.Object2BooleanMapCodec;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import javax.annotation.Nonnull;

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
            .append(new KeyedCodec<>("BondingLevelValuesOverride", Codec.FLOAT_ARRAY),
                    (asset, bondingLevelValues) -> asset.bondingLevelValuesOverride = bondingLevelValues,
                    PetType::getBondingLevelValues)
            .documentation("TODO")
            .add()
            .documentation("TODO");
    public static final AssetCodec<String, PetType> CODEC = CODEC_BUILDER.build();
    private static AssetStore<String, PetType, DefaultAssetMap<String, PetType>> ASSET_STORE;
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache<>(new AssetKeyValidator<>(BondingActivity::getAssetStore));

    public enum JoinsFlock {
        Always, FollowOnly, Never
    }

    public enum AbandonBehavior {
        Despawn, UntameIfSpawned
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
    protected String[] bondingActivities;
    protected float[] bondingLevelValuesOverride;
    protected JoinsFlock joinsFlock = JoinsFlock.FollowOnly;
    protected AbandonBehavior abandonBehavior = AbandonBehavior.UntameIfSpawned;

    private PetType() {
        featureFlags = new Object2BooleanArrayMap<>();
    }

    public boolean hasFeatureFlag(PetFeatureFlag flag) {
        return featureFlags.getOrDefault(flag, flag.getDefaultValue());
    }

    // TODO: Is there a way to automatically map these to BondingActivity assets?
    // TODO: Or even better, Map<BondingActivityType, BondingActivity>
    public String[] getBondingActivities() {
        return bondingActivities;
    }

    public AbandonBehavior getAbandonBehavior() {
        return abandonBehavior;
    }

    @Nonnull
    public float[] getBondingLevelValues() {
        if (bondingLevelValuesOverride == null) {
            return CherishedCreaturesConfig.get().getDefaultBondingLevelValues();
        }
        return bondingLevelValuesOverride;
    }

    @Override
    public String getId() {
        return id;
    }

    public enum PetFeatureFlag {
        Bonding(false),
        Immortal(false), // Pet that cannot take damage or die, so health doesn't matter
        FollowModeControls(true),
        SummonControls(true),
        HealsOnSpawn(false);

        private final boolean defaultValue;

        PetFeatureFlag(boolean defaultValue) {
            this.defaultValue = defaultValue;
        }

        public boolean getDefaultValue() {
            return defaultValue;
        }
    }
}
