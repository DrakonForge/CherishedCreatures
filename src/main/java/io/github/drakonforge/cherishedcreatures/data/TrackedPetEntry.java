package io.github.drakonforge.cherishedcreatures.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
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
import org.bson.BsonDocument;

public class TrackedPetEntry implements Cloneable {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static final BuilderCodec<TrackedPetEntry> CODEC = BuilderCodec.builder(
                    TrackedPetEntry.class, TrackedPetEntry::new)
            .append(new KeyedCodec<>("UUID", Codec.UUID_STRING, true),
                    (data, uuid) -> data.uuid = uuid, TrackedPetEntry::getUuid)
            .add()
            // TODO: Deprecated :(
            .append(new KeyedCodec<>("SavedEntity", Codec.BSON_DOCUMENT), (data, bson) -> data.savedEntity = bson, data -> data.savedEntity)
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

    public static BsonDocument saveEntityToBson(Store<EntityStore> store, Ref<EntityStore> ref) {
        Holder<EntityStore> copy = store.copyEntity(ref);
        copy.removeComponent(TransformComponent.getComponentType());
        return EntityStore.REGISTRY.serialize(copy);
    }

    @Nullable
    public static Holder<EntityStore> loadEntityFromBson(BsonDocument entityDocument) {
        return EntityStore.REGISTRY.deserialize(entityDocument);
    }

    private UUID uuid = null;
    /**
     * The saved entity as BSON which persists even when the pet unloads. This is the only
     * serialized field with the pet data.
     */
    @Nullable
    private BsonDocument savedEntity = null;
    /**
     * A ref to the active, loaded entity.
     */
    @Nullable
    private Ref<EntityStore> entityRef = null;
    /**
     * A holder for the entity which is cached from savedEntity, non-persistent.
     * Holders are entities which have not yet been added to a system (are not active).
     */
    @Nullable
    private Holder<EntityStore> holder = null;
    private Status status = Status.ALIVE; // TODO: Not actually sure if we should store this separately

    public UUID getUuid() {
        return uuid;
    }

    public Holder<EntityStore> getOrCreateHolder(Store<EntityStore> store) {
        if (entityRef != null && entityRef.isValid()) {
            holder = store.copyEntity(entityRef);
        }
        if (holder != null) {
            return holder;
        }
        Holder<EntityStore> newHolder = loadEntityFromBson(savedEntity);
        // TODO: Handle this more gracefully
        if (newHolder == null) {
            throw new IllegalStateException("Failed to load entity from bson");
        }
        holder = newHolder;
        return newHolder;
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
        this.savedEntity = saveEntityToBson(store, entityRef);
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
        Holder<EntityStore> holder = getOrCreateHolder(store);
        return holder.getComponent(componentType);
    }

    public boolean isActive() {
        return entityRef != null && entityRef.isValid();
    }

    public boolean isValid() {
        return savedEntity != null || entityRef != null;
    }

    @Override
    protected TrackedPetEntry clone() {
        TrackedPetEntry clone = new TrackedPetEntry();
        clone.uuid = uuid;
        clone.savedEntity = savedEntity;
        clone.entityRef = entityRef;
        return clone;
    }
}
