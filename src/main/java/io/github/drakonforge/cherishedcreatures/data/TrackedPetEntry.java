package io.github.drakonforge.cherishedcreatures.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.UUID;
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

    @Nullable
    public static TrackedPetEntry createEntryFor(Store<EntityStore> store, Ref<EntityStore> ref) {
        TrackedPetEntry entry = new TrackedPetEntry();
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
        if (uuidComponent == null) {
            return null;
        }
        entry.uuid = uuidComponent.getUuid();
        entry.entityRef = ref;
        entry.saveEntity(store);
        return entry;
    }

    public static BsonDocument saveEntityToBson(Store<EntityStore> store, Ref<EntityStore> ref) {
        Holder<EntityStore> copy = store.copyEntity(ref);
        copy.removeComponent(TransformComponent.getComponentType());
        BsonDocument entityDocument = EntityStore.REGISTRY.serialize(copy);
        return entityDocument;
    }

    @Nullable
    public static Holder<EntityStore> loadEntityFromBson(BsonDocument entityDocument) {
        return EntityStore.REGISTRY.deserialize(entityDocument);
    }

    // TODO: Create codec

    private UUID uuid = null;
    @Nullable
    private BsonDocument savedEntity = null;
    @Nullable
    private Ref<EntityStore> entityRef = null;
    private Status status = Status.ALIVE;

    public UUID getUuid() {
        return uuid;
    }

    public Holder<EntityStore> createHolder(Store<EntityStore> store) {
        if (entityRef != null && entityRef.isValid()) {
            return store.copyEntity(entityRef);
        }
        Holder<EntityStore> newEntity = loadEntityFromBson(savedEntity);
        // TODO: Handle this more gracefully
        if (newEntity == null) {
            throw new IllegalStateException("Failed to load entity from bson");
        }
        return newEntity;
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
