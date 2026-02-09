package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.util.OfflinePlayerHelpers;
import java.util.UUID;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PetUpdateTrackerSystem extends RefSystem<EntityStore> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    public void onEntityAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl AddReason addReason,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PetComponent petComponent = store.getComponent(ref, PetComponent.getComponentType());
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());

        assert petComponent != null;
        assert uuidComponent != null;

        UUID uuid = uuidComponent.getUuid();
        UUID ownerUuid = petComponent.getOwnerUuid();
        if (ownerUuid == null) {
            LOGGER.atSevere().log("When adding entity, Owner UUID is null for pet " + uuid);
            return;
        }
        PlayerPetTracker petTracker = OfflinePlayerHelpers.getComponent(store, ownerUuid, PlayerPetTracker.getComponentType());
        if (petTracker == null) {
            return;
        }
        TrackedPetEntry trackedPetEntry = petTracker.getPetEntry(uuid);
        if (trackedPetEntry == null) {
            return;
        }
        trackedPetEntry.setEntityRef(ref);
        // trackedPetEntry.saveEntityFromRef(store, ref);

        // store.getResource(CherishedCreaturesPlugin.get().getPetUpdateQueueResourceType()).deliverUpdatesForPet(store, ref);
        // OfflinePlayerHelpers.saveIfOffline(ownerUuid);
    }

    @Override
    public void onEntityRemove(@NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PetComponent petComponent = store.getComponent(ref, PetComponent.getComponentType());
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());

        assert petComponent != null;
        assert uuidComponent != null;

        UUID uuid = uuidComponent.getUuid();
        UUID ownerUuid = petComponent.getOwnerUuid();
        if (ownerUuid == null) {
            LOGGER.atSevere().log("When adding entity, Owner UUID is null for pet " + uuid);
            return;
        }
        PlayerPetTracker petTracker = OfflinePlayerHelpers.getComponent(store, ownerUuid, PlayerPetTracker.getComponentType());
        if (petTracker == null) {
            return;
        }
        TrackedPetEntry trackedPetEntry = petTracker.getPetEntry(uuid);
        if (trackedPetEntry == null) {
            return;
        }
        trackedPetEntry.attemptSaveEntityFromLive(store);
        trackedPetEntry.setEntityRef(null);

        OfflinePlayerHelpers.saveIfOffline(ownerUuid);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(UUIDComponent.getComponentType(), PetComponent.getComponentType());
    }
}
