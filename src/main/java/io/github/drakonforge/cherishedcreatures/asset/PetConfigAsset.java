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

public class PetConfigAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, PetConfigAsset>> {

    private static final AssetBuilderCodec.Builder<String, PetConfigAsset> CODEC_BUILDER = AssetBuilderCodec.builder(
            PetConfigAsset.class, PetConfigAsset::new, Codec.STRING, (asset, id) -> asset.id = id, PetConfigAsset::getId, (asset, data) -> asset.extraData = data, asset -> asset.extraData);
    public static final AssetCodec<String, PetConfigAsset> CODEC = CODEC_BUILDER.build();
    private static AssetStore<String, PetConfigAsset, DefaultAssetMap<String, PetConfigAsset>> ASSET_STORE;

    public static AssetStore<String, PetConfigAsset, DefaultAssetMap<String, PetConfigAsset>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(PetConfigAsset.class);
        }
        return ASSET_STORE;
    }

    public static void register() {
        if (AssetRegistry.getAssetStore(PetConfigAsset.class) != null) {
            return;
        }
        AssetRegistry.register(
                HytaleAssetStore.builder(PetConfigAsset.class, new DefaultAssetMap<>()).setPath("PetConfig").setCodec(CODEC).setKeyFunction(PetConfigAsset::getId).build());
    }

    protected String id;
    protected AssetExtraInfo.Data extraData;

    @Override
    public String getId() {
        return id;
    }
}
