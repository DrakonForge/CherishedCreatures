package io.github.drakonforge.cherishedcreatures.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
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
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

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
            .append(new KeyedCodec<>("LastKnownPos", Vector3d.CODEC), (data, pos) -> data.lastKnownPos = pos, data -> data.lastKnownPos)
            .add()
            .append(new KeyedCodec<>("WorldUuid", Codec.UUID_STRING), (data, uuid) -> data.worldUuid = uuid, data -> data.worldUuid)
            .add()
            .build();

    // TODO: Do on spawn instead of on despawn?
    public void updateHolder(Holder<EntityStore> holder) {
        // Super hacky solution to ensure that we get a properly serialized component
        StoredCodec<Holder<EntityStore>> codec = new StoredCodec<>(EntityStore.HOLDER_CODEC_KEY);
        ExtraInfo extraInfo = new ExtraInfo();
        Holder<EntityStore> newHolder = codec.decode(codec.encode(holder, extraInfo), extraInfo);
        if (newHolder == null) {
            LOGGER.atWarning().log("Failed to encode/decode object");
            this.holder = EntityStore.REGISTRY.newHolder();
        } else {
            this.holder = newHolder;
        }
        // this.holder = holder.cloneSerializable(EntityStore.REGISTRY.getData());
    }

    public enum Status {
        ALIVE, STORED, UNKNOWN, DEAD
    }

    private TrackedPetEntry() {}

    @Nullable
    public static TrackedPetEntry createEntryFor(Store<EntityStore> store, Ref<EntityStore> ref) {
        TrackedPetEntry entry = new TrackedPetEntry();
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (uuidComponent == null || transformComponent == null) {
            return null;
        }
        // TODO: Validate for Pet and PetType components here?
        entry.uuid = uuidComponent.getUuid();
        entry.worldUuid = store.getExternalData().getWorld().getWorldConfig().getUuid();
        entry.lastKnownPos = transformComponent.getPosition().clone();

        entry.saveEntityFromRef(store, ref);
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
    private Status status = Status.ALIVE;
    @Nullable
    private UUID worldUuid;
    @Nullable
    private Vector3d lastKnownPos;

    public UUID getUuid() {
        return uuid;
    }

    @NonNullDecl
    public Holder<EntityStore> getHolder() {
        return holder.clone();
    }

    public void setEntityRef(@Nullable Ref<EntityStore> entityRef) {
        LOGGER.atInfo().log("Setting entity ref for " + uuid + " to " + (entityRef == null ? "Null" : entityRef.getIndex()));
        this.entityRef = entityRef;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setLastKnownPos(@Nullable Vector3d lastKnownPos) {
        this.lastKnownPos = lastKnownPos;
    }

    public void setWorldUuid(@Nullable UUID worldUuid) {
        this.worldUuid = worldUuid;
    }

    public void clearPosData() {
        this.lastKnownPos = null;
        this.worldUuid = null;
    }

    // Validates the TrackedPetEntry properties using the holder.
    private void validateTrackedPetEntry() {
        LOGGER.atInfo().log("Validating " + uuid);
        if (holder.getComponent(DeathComponent.getComponentType()) == null) {
            if (status == Status.DEAD) {
                setStatus(Status.UNKNOWN);
            }
        } else {
            // TODO: Might be based on config
            setStatus(Status.DEAD);
        }
    }

    // TODO: Maybe think about throttling this?
    public void saveEntityFromRef(Store<EntityStore> store, Ref<EntityStore> ref) {
        if (ref == null || !ref.isValid()) {
            throw new IllegalStateException("Ref is invalid or null");
        }
        setEntityRef(ref);
        if (!store.getArchetype(ref).hasSerializableComponents(store.getRegistry().getData())) {
            LOGGER.atInfo().log("Nothing to serialize for " + uuid + ", skipping save");
            holder = EntityStore.REGISTRY.newHolder();
            return;
        }
        LOGGER.atInfo().log("Saving " + uuid + " from ref");
        holder = store.copySerializableEntity(ref); // Save only the serializable components, ignore everything else
        validateTrackedPetEntry();
    }

    public void attemptSaveEntityFromLive(Store<EntityStore> store) {
        if (entityRef == null || !entityRef.isValid()) {
            return;
        }
        saveEntityFromRef(store, entityRef);
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
        return holder.getComponent(componentType);
    }

    // Returns whether the entity is currently loaded in the world.
    public boolean isLoaded() {
        return entityRef != null && entityRef.isValid();
    }

    public Status getStatus() {
        return status;
    }

    public Vector3d getLastKnownPos() {
        return lastKnownPos;
    }

    public UUID getWorldUuid() {
        return worldUuid;
    }

    // Should be called before using PetHelpers.summonPet
    public boolean canSummonViaMenu(Store<EntityStore> store) {
        PetType petType = getPetType(store);
        // TODO: Also check pet type summon rules
        return status == Status.STORED || status == Status.ALIVE;
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
