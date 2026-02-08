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
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;

public class BondingActivity implements
        JsonAssetWithMap<String, DefaultAssetMap<String, BondingActivity>> {

    private static final AssetBuilderCodec.Builder<String, BondingActivity> CODEC_BUILDER = AssetBuilderCodec.builder(
                    BondingActivity.class, BondingActivity::new, Codec.STRING, (asset, id) -> asset.id = id,
                    BondingActivity::getId, (asset, data) -> asset.extraData = data,
                    asset -> asset.extraData)
            .append(new KeyedCodec<>("BaseXp", Codec.FLOAT), (asset, xp) -> asset.baseXp = xp,
                    BondingActivity::getBaseXp)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("Cooldown", Codec.FLOAT),
                    (asset, cooldown) -> asset.cooldown = cooldown, BondingActivity::getCooldown)
            .documentation("TODO")
            .add()
            .documentation("TODO");
    public static final AssetCodec<String, BondingActivity> CODEC = CODEC_BUILDER.build();
    private static AssetStore<String, BondingActivity, DefaultAssetMap<String, BondingActivity>> ASSET_STORE;

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
    protected AssetExtraInfo.Data extraData;
    protected float baseXp;
    protected float cooldown;

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
}
