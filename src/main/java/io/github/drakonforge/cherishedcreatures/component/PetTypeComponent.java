package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * Component to represent a pet that can be tamed. Does not necessarily have to be tamed.
 * Points to a PetType asset which has more configuration on how it works.
 */
// TODO: Maybe look into making this a core component?
public class PetTypeComponent implements Component<EntityStore> {

    public static final BuilderCodec<PetTypeComponent> CODEC = BuilderCodec.builder(PetTypeComponent.class,
                    PetTypeComponent::new)
            .append(new KeyedCodec<>("PetType", Codec.STRING),
                    (data, type) -> data.petTypeId = type, PetTypeComponent::getPetTypeId)
            .add()
            .build();

    public static ComponentType<EntityStore, PetTypeComponent> getComponentType() {
        return CherishedCreaturesPlugin.get().getPetTypeComponentType();
    }

    private String petTypeId;

    public String getPetTypeId() {
        return petTypeId;
    }

    @Nonnull
    public PetType getPetType() {
        PetType petType = PetType.getAssetStore().getAssetMap().getAsset(this.petTypeId);
        return petType != null ? petType : PetType.DEFAULT;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        PetTypeComponent clone = new PetTypeComponent();
        clone.petTypeId = petTypeId;
        return clone;
    }
}
