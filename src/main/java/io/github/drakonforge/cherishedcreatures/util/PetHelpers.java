package io.github.drakonforge.cherishedcreatures.util;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry.Status;

public final class PetHelpers {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public enum TameResult {
        SUCCESS,
        FAIL_MISSING_COMPONENTS,
        FAIL_NOT_TAMEABLE,
        FAIL_ALREADY_TAMED_BY_SELF
    }

    public static TameResult attemptTame(Store<EntityStore> store, Ref<EntityStore> playerRef, Ref<EntityStore> petRef) {
        PlayerPetTracker playerPetTracker = store.getComponent(playerRef, PlayerPetTracker.getComponentType());
        UUIDComponent uuidComponent = store.getComponent(playerRef, UUIDComponent.getComponentType());
        if (playerPetTracker == null || uuidComponent == null) {
            return TameResult.FAIL_MISSING_COMPONENTS;
        }

        PetTypeComponent petTypeComponent = store.getComponent(petRef, PetTypeComponent.getComponentType());
        if (petTypeComponent == null) {
            return TameResult.FAIL_NOT_TAMEABLE;
        }
        PetComponent existingPetComponent = store.getComponent(petRef, PetComponent.getComponentType());
        if (existingPetComponent != null && existingPetComponent.getOwnerUuid().equals(uuidComponent.getUuid())) {
            return TameResult.FAIL_ALREADY_TAMED_BY_SELF;
        }

        TrackedPetEntry entry = TrackedPetEntry.createEntryFor(store, petRef);
        if (entry == null) {
            return TameResult.FAIL_MISSING_COMPONENTS;
        }
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            // Other components are handled in RegisterPetComponentsSystem
            PetComponent petComponent = store.ensureAndGetComponent(petRef, PetComponent.getComponentType());
            petComponent.setOwnerUuid(uuidComponent.getUuid());
        });

        // TODO: Change role

        playerPetTracker.addPetEntry(entry);
        return TameResult.SUCCESS;
    }

    // Summons the pet from the entry. This forcefully summons it, so it does not perform any checks
    // to determine whether the pet should be summonable.
    public static void summonPet(TrackedPetEntry entry, Store<EntityStore> store, TransformComponent transform) {
        Ref<EntityStore> existingEntity = store.getExternalData().getRefFromUUID(entry.getUuid());
        // TODO: Probably don't need to remove + re-add if the entity is loaded, just teleport it
        // TODO: Not sure if we need isLoaded or getRefFromUUID here. Probably not both
        if (existingEntity != null && existingEntity.isValid()) {
            if (!entry.isLoaded()) {
                LOGGER.atWarning().log("Pet is active but pet tracker is not in sync, re-syncing");
                entry.setEntityRef(existingEntity);
            }
            entry.saveEntity(store);
            store.removeEntity(existingEntity, RemoveReason.UNLOAD);
        }
        Holder<EntityStore> newEntity = entry.updateAndGetHolder(store);
        newEntity.putComponent(TransformComponent.getComponentType(), transform.clone());
        entry.setStatus(Status.ACTIVE);
        entry.setLastKnownPos(transform.getPosition().clone());
        entry.setWorldUuid(store.getExternalData().getWorld().getWorldConfig().getUuid());
        Ref<EntityStore> newEntityRef = store.addEntity(newEntity, AddReason.LOAD);
        entry.setEntityRef(newEntityRef);
    }

    public static boolean unsummonPet(TrackedPetEntry entry, Store<EntityStore> store) {
        Ref<EntityStore> existingEntity = store.getExternalData().getRefFromUUID(entry.getUuid());
        if (existingEntity == null || !existingEntity.isValid()) {
            return false;
        }

        entry.saveEntity(store);
        entry.clearPosData();
        entry.setStatus(Status.STORED);
        store.removeEntity(existingEntity, RemoveReason.REMOVE);
        return true;
    }
}
