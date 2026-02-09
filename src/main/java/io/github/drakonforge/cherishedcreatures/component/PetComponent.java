package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

// Base pet info for a tamed pet
public class PetComponent implements Component<EntityStore> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static final BuilderCodec<PetComponent> CODEC = BuilderCodec.builder(PetComponent.class,
                    PetComponent::new)
            .append(new KeyedCodec<>("OwnerUUID", Codec.UUID_STRING, true),
                    PetComponent::setOwnerUuid, PetComponent::getOwnerUuid)
            .addValidator(Validators.nonNull())
            .add()
            .append(new KeyedCodec<>("Name", Codec.STRING), PetComponent::setName,
                    PetComponent::getName)
            .add()
            .build();

    public static ComponentType<EntityStore, PetComponent> getComponentType() {
        return CherishedCreaturesPlugin.get().getPetComponentType();
    }

    private PetComponent() {}

    public PetComponent(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    private UUID ownerUuid;
    private String name;

    public void setOwnerUuid(@Nonnull UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public String getName() {
        return name;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        PetComponent clone = new PetComponent();
        clone.ownerUuid = ownerUuid;
        clone.name = name;
        return clone;
    }
}
