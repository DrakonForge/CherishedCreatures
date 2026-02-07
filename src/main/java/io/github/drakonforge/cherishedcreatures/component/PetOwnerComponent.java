package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import java.util.UUID;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PetOwnerComponent implements Component<EntityStore> {

    public static final BuilderCodec<PetOwnerComponent> CODEC = BuilderCodec.builder(PetOwnerComponent.class, PetOwnerComponent::new).append(new KeyedCodec<>("OwnerUUID",
            BuilderCodec.UUID_STRING, true), PetOwnerComponent::setOwnerUuid, PetOwnerComponent::getOwnerUuid).add().build();

    public static ComponentType<EntityStore, PetOwnerComponent> getComponentType() {
        return CherishedCreaturesPlugin.get().getPetOwnerComponentType();
    }

    private UUID ownerUuid;

    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        PetOwnerComponent clone = new PetOwnerComponent();
        clone.ownerUuid = ownerUuid;
        return clone;
    }
}
