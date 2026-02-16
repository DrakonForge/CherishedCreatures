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
import io.github.drakonforge.cherishedcreatures.data.PetActivityType;

public class PetActivity implements
        JsonAssetWithMap<String, DefaultAssetMap<String, PetActivity>> {

    private static final AssetBuilderCodec.Builder<String, PetActivity> CODEC_BUILDER = AssetBuilderCodec.builder(
                    PetActivity.class, PetActivity::new, Codec.STRING, (asset, id) -> asset.id = id,
                    PetActivity::getId, (asset, data) -> asset.extraData = data,
                    asset -> asset.extraData)
            .append(new KeyedCodec<>("Type", new EnumCodec<>(
                    PetActivityType.class)), (asset, type) -> asset.petActivityType = type, PetActivity::getPetActivityType)
            .documentation("The type of pet activity, which affects what events can trigger it.")
            .add()
            .append(new KeyedCodec<>("BaseXp", Codec.FLOAT), (asset, xp) -> asset.baseXp = xp,
                    PetActivity::getBaseXp)
            .documentation("The base bonding XP gained for this activity. Can be modified by Happiness multipliers.")
            .add()
            .append(new KeyedCodec<>("Cooldown", Codec.FLOAT),
                    (asset, cooldown) -> asset.cooldown = cooldown, PetActivity::getCooldown)
            .documentation("The cooldown in seconds for this activity.")
            .add()
            .append(new KeyedCodec<>("HappinessGain", Codec.FLOAT),
                    (asset, gain) -> asset.happinessGain = gain, PetActivity::getHappinessGain)
            .documentation("The amount of Happiness gained for this activity.")
            .add()
            .documentation("TODO");
    public static final AssetCodec<String, PetActivity> CODEC = CODEC_BUILDER.build();
    private static AssetStore<String, PetActivity, DefaultAssetMap<String, PetActivity>> ASSET_STORE;
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache<>(new AssetKeyValidator<>(
            PetActivity::getAssetStore));

    public static AssetStore<String, PetActivity, DefaultAssetMap<String, PetActivity>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(PetActivity.class);
        }
        return ASSET_STORE;
    }

    public static void register() {
        if (AssetRegistry.getAssetStore(PetActivity.class) != null) {
            return;
        }
        AssetRegistry.register(
                HytaleAssetStore.builder(PetActivity.class, new DefaultAssetMap<>())
                        .setPath("PetActivity")
                        .setCodec(CODEC)
                        .setKeyFunction(PetActivity::getId)
                        .build());
    }

    protected String id;
    protected PetActivityType petActivityType = PetActivityType.Custom;
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

    public PetActivityType getPetActivityType() {
        return petActivityType;
    }
}
