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
import io.github.drakonforge.cherishedcreatures.data.BondingActivityType;

public class BondingActivity implements
        JsonAssetWithMap<String, DefaultAssetMap<String, BondingActivity>> {

    private static final AssetBuilderCodec.Builder<String, BondingActivity> CODEC_BUILDER = AssetBuilderCodec.builder(
                    BondingActivity.class, BondingActivity::new, Codec.STRING, (asset, id) -> asset.id = id,
                    BondingActivity::getId, (asset, data) -> asset.extraData = data,
                    asset -> asset.extraData)
            .append(new KeyedCodec<>("Type", new EnumCodec<>(BondingActivityType.class)), (asset, type) -> asset.bondingActivityType = type, BondingActivity::getBondingActivityType)
            .documentation("The type of bonding activity, which affects what events can trigger it.")
            .add()
            .append(new KeyedCodec<>("BaseXp", Codec.FLOAT), (asset, xp) -> asset.baseXp = xp,
                    BondingActivity::getBaseXp)
            .documentation("The base bonding XP gained for this bonding activity. Can be modified by Happiness multipliers.")
            .add()
            .append(new KeyedCodec<>("Cooldown", Codec.FLOAT),
                    (asset, cooldown) -> asset.cooldown = cooldown, BondingActivity::getCooldown)
            .documentation("The cooldown in seconds for this bonding activity.")
            .add()
            .append(new KeyedCodec<>("HappinessGain", Codec.FLOAT),
                    (asset, gain) -> asset.happinessGain = gain, BondingActivity::getHappinessGain)
            .documentation("The amount of Happiness gained for this bonding activity.")
            .add()
            .documentation("TODO");
    public static final AssetCodec<String, BondingActivity> CODEC = CODEC_BUILDER.build();
    private static AssetStore<String, BondingActivity, DefaultAssetMap<String, BondingActivity>> ASSET_STORE;
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache<>(new AssetKeyValidator<>(BondingActivity::getAssetStore));

    public static AssetStore<String, BondingActivity, DefaultAssetMap<String, BondingActivity>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(BondingActivity.class);
        }
        return ASSET_STORE;
    }

    public static void register() {
        if (AssetRegistry.getAssetStore(BondingActivity.class) != null) {
            return;
        }
        AssetRegistry.register(
                HytaleAssetStore.builder(BondingActivity.class, new DefaultAssetMap<>())
                        .setPath("BondingActivity")
                        .setCodec(CODEC)
                        .setKeyFunction(BondingActivity::getId)
                        .build());
    }

    protected String id;
    protected BondingActivityType bondingActivityType = BondingActivityType.Custom;
    protected AssetExtraInfo.Data extraData;
    protected float baseXp;
    protected float cooldown;
    protected float happinessGain;

    @Override
    public String getId() {
        return id;
    }

    public float getBaseXp() {
        return baseXp;
    }

    public float getCooldown() {
        return cooldown;
    }

    public float getHappinessGain() {
        return happinessGain;
    }

    public BondingActivityType getBondingActivityType() {
        return bondingActivityType;
    }
}
