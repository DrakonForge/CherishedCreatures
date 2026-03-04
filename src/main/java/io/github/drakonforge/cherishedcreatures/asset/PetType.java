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
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesConfig;
import io.github.drakonforge.cherishedcreatures.util.Object2BooleanMapCodec;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public class PetType implements JsonAssetWithMap<String, DefaultAssetMap<String, PetType>> {

    public static final PetType DEFAULT = new PetType();
    private static final AssetBuilderCodec.Builder<String, PetType> CODEC_BUILDER = AssetBuilderCodec.builder(
                    PetType.class, PetType::new, Codec.STRING, (asset, id) -> asset.id = id, PetType::getId,
                    (asset, data) -> asset.extraData = data, asset -> asset.extraData)
            .append(new KeyedCodec<>("PetActivities", Codec.STRING_ARRAY),
                    (asset, petActivities) -> asset.petActivities = petActivities,
                    asset -> asset.petActivities)
            .addValidatorLate(() -> PetActivity.VALIDATOR_CACHE.getArrayValidator().late())
            .addValidator(Validators.uniqueInArray())
            .documentation("What kinds of pet activities that pets of this type can gain bonding XP and happiness from.")
            .add()
            .append(new KeyedCodec<>("FeatureFlags", new Object2BooleanMapCodec<>(new EnumCodec<>(
                            PetFeatureFlag.class), Object2BooleanArrayMap::new)),
                    (asset, featureFlags) -> asset.featureFlags = featureFlags,
                    asset -> asset.featureFlags)
            .documentation("A list of toggleable features for this pet type.")
            .add()
            .append(new KeyedCodec<>("BondingLevelValuesOverride", Codec.FLOAT_ARRAY),
                    (asset, bondingLevelValues) -> asset.bondingLevelValuesOverride = bondingLevelValues,
                    PetType::getBondingLevelValues)
            .documentation("A list of bonding XP level thresholds which overrides the server default for this pet type only.")
            .add()
            .append(new KeyedCodec<>("BaseHealthModifier", NumericAttribute.CODEC), (asset, baseHealth) -> asset.baseHealthModifier = baseHealth, PetType::getBaseHealthModifier)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("BaseStaminaModifier", NumericAttribute.CODEC), (asset, baseStamina) -> asset.baseStaminaModifier = baseStamina, PetType::getBaseStaminaModifier)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("MountBaseSpeed", NumericAttribute.CODEC), (asset, baseSpeed) -> asset.mountBaseSpeed = baseSpeed, PetType::getMountBaseSpeed)
            .documentation("Only for mounts with AdvancedMountHandling enabled. The base speed of the mount when ridden by the player.")
            .add()
            .append(new KeyedCodec<>("MountGaitAcceleration", NumericAttribute.CODEC), (asset, acceleration) -> asset.mountGaitAcceleration = acceleration, PetType::getMountGaitAcceleration)
            .documentation("Only for mounts with AdvancedMountHandling enabled. How quickly the mount's speed multiplier due to gait changes per second.")
            .add()
            .documentation("The pet type determines the configuration of an NPC when it is tamed as a pet.");
    public static final AssetCodec<String, PetType> CODEC = CODEC_BUILDER.build();
    private static AssetStore<String, PetType, DefaultAssetMap<String, PetType>> ASSET_STORE;
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache<>(new AssetKeyValidator<>(
            PetActivity::getAssetStore));

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
    protected String[] petActivities;
    protected float[] bondingLevelValuesOverride;
    protected JoinsFlock joinsFlock = JoinsFlock.FollowOnly;
    protected AbandonBehavior abandonBehavior = AbandonBehavior.UntameIfSpawned;
    // TODO: Actually go apply these
    protected NumericAttribute baseHealthModifier = new NumericAttribute(-5.0f, 5.0f, 0.0f, 1.0f, -1.0f);
    protected NumericAttribute baseStaminaModifier = new NumericAttribute(-5.0f, 5.0f, 0.0f, 1.0f, -1.0f);
    protected NumericAttribute mountBaseSpeed = new NumericAttribute(5.0f, 15.0f, 10.0f, 2.0f, 1.0f);
    protected NumericAttribute mountGaitAcceleration = new NumericAttribute(0.5f, 1.5f, 1.0f, 0.0f, 0.1f);

    private PetType() {
        featureFlags = new Object2BooleanArrayMap<>();
    }

    public boolean hasFeatureFlag(PetFeatureFlag flag) {
        return featureFlags.getOrDefault(flag, flag.getDefaultValue());
    }

    // TODO: Is there a way to automatically map these to BondingActivity assets?
    // TODO: Or even better, Map<BondingActivityType, BondingActivity>
    public String[] getPetActivities() {
        return petActivities;
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

    public NumericAttribute getBaseHealthModifier() {
        return baseHealthModifier;
    }

    public NumericAttribute getBaseStaminaModifier() {
        return baseStaminaModifier;
    }

    public NumericAttribute getMountBaseSpeed() {
        return mountBaseSpeed;
    }

    public NumericAttribute getMountGaitAcceleration() {
        return mountGaitAcceleration;
    }

    @Override
    public String getId() {
        return id;
    }

    public enum PetFeatureFlag implements Supplier<String> {
        Bonding(true, "Whether this pet uses the bonding system."),
        Immortal(false, "If the pet cannot take damage or die. Will stop displaying health-related UI."),
        FollowModeControls(true, "Whether this pet's follow mode can be toggled via UI."),
        SummonControls(true, "Whether this pet can be summoned or unsummoned via UI."),
        AdvancedMountHandling(false, "If this is a mount, uses the advanced mount handling system."),
        HealsOnSpawn(false, "Whether this pet should fully heal upon being spawned in.");

        private final boolean defaultValue;
        private final String description;

        PetFeatureFlag(boolean defaultValue, String description) {
            this.defaultValue = defaultValue;
            this.description = description;
        }

        public boolean getDefaultValue() {
            return defaultValue;
        }

        @Override
        public String get() {
            return this.description;
        }
    }
}
