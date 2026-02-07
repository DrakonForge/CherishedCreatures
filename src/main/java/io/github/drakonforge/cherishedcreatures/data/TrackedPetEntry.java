package io.github.drakonforge.cherishedcreatures.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.store.StoredCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TrackedPetEntry implements Cloneable {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static final BuilderCodec<TrackedPetEntry> CODEC = BuilderCodec.builder(
                    TrackedPetEntry.class, TrackedPetEntry::new)
            .append(new KeyedCodec<>("UUID", Codec.UUID_STRING, true),
                    (data, uuid) -> data.uuid = uuid, TrackedPetEntry::getUuid)
            .add()
            .append(new KeyedCodec<>("SavedEntity", new StoredCodec<>(EntityStore.HOLDER_CODEC_KEY), true), (data, holder) -> data.holder = holder, data -> data.holder)
            .add()
            .append(new KeyedCodec<>("Status", new EnumCodec<>(Status.class)), (data, status) -> data.status = status, data -> data.status)
            .add()
            .build();

    public enum Status {
        ALIVE, DEAD
    }

    private TrackedPetEntry() {}

    @Nullable
    public static TrackedPetEntry createEntryFor(Store<EntityStore> store, Ref<EntityStore> ref) {
        TrackedPetEntry entry = new TrackedPetEntry();
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
        if (uuidComponent == null) {
            return null;
        }
        // TODO: Validate for Pet and PetType components here?
        entry.uuid = uuidComponent.getUuid();
        entry.entityRef = ref;
        entry.saveEntity(store);
        return entry;
    }

    private UUID uuid = null;
    /**
     * A ref to the active, loaded entity.
     */
    @Nullable
    private Ref<EntityStore> entityRef = null;
    /**
     * A serializable holder for the entity.
     * Holders are entities which have not yet been added to a system (are not active).
     */
    @Nonnull
    private Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
    private Status status = Status.ALIVE; // TODO: Not actually sure if we should store this separately

    public UUID getUuid() {
        return uuid;
    }

    public Holder<EntityStore> updateAndGetHolder(Store<EntityStore> store) {
        if (entityRef != null && entityRef.isValid()) {
            saveEntity(store);
        }
        return holder;
    }

    public void setEntityRef(@Nullable Ref<EntityStore> entityRef) {
        this.entityRef = entityRef;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void saveEntity(Store<EntityStore> store) {
        if (entityRef == null) {
            throw new IllegalStateException("No entity to save");
        }
        LOGGER.atInfo().log("Saved pet data");
        holder = store.copyEntity(entityRef);

        // Remove components it should not be keeping
        holder.removeComponent(TransformComponent.getComponentType());
    }

    public Status getStatus() {
        return status;
    }

    @Nonnull
    public PetType getPetType(Store<EntityStore> store) {
        PetTypeComponent petComponent = getComponent(store, PetTypeComponent.getComponentType());
        if (petComponent == null) {
            LOGGER.atWarning().log("Pet type component did not exist for tracked pet entry");
            return PetType.DEFAULT;
        }
        return petComponent.getPetType();
    }

    @Nullable
    public <T extends Component<EntityStore>> T getComponent(Store<EntityStore> store, ComponentType<EntityStore, T> componentType) {
        if (entityRef != null && entityRef.isValid()) {
            return store.getComponent(entityRef, componentType);
        }
        Holder<EntityStore> holder = updateAndGetHolder(store);
        return holder.getComponent(componentType);
    }

    public boolean isActive() {
        return entityRef != null && entityRef.isValid();
    }

    @Override
    protected TrackedPetEntry clone() {
        TrackedPetEntry clone = new TrackedPetEntry();
        clone.uuid = uuid;
        clone.holder = holder;
        clone.entityRef = entityRef;
        clone.status = status;
        return clone;
    }
}
