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
        if (existingPetComponent != null && uuidComponent.getUuid().equals(existingPetComponent.getOwnerUuid())) {
            return TameResult.FAIL_ALREADY_TAMED_BY_SELF;
        }

        TrackedPetEntry entry = TrackedPetEntry.createEntryFor(store, petRef);
        if (entry == null) {
            return TameResult.FAIL_MISSING_COMPONENTS;
        }
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            // Other components are handled in RegisterPetComponentsSystem
            store.putComponent(petRef, PetComponent.getComponentType(), new PetComponent(uuidComponent.getUuid()));
        });

        // TODO: Change role

        playerPetTracker.addPetEntry(entry);
        return TameResult.SUCCESS;
    }

    // Summons the pet from the entry. This forcefully summons it, so it does not perform any checks
    // to determine whether the pet should be summonable.
    public static void summonPet(TrackedPetEntry entry, Store<EntityStore> store, TransformComponent spawnTransform) {
        Ref<EntityStore> existingEntity = store.getExternalData().getRefFromUUID(entry.getUuid());
        if (existingEntity != null && existingEntity.isValid()) {
            if (!entry.isLoaded()) {
                LOGGER.atWarning().log("Pet is loaded but pet tracker is not in sync, re-syncing");
                entry.setEntityRef(existingEntity);
            }
            TransformComponent transform = store.getComponent(existingEntity, TransformComponent.getComponentType());
            if (transform != null) {
                transform.setPosition(spawnTransform.getPosition().clone());
                entry.setStatus(Status.ALIVE);
                entry.setLastKnownPos(spawnTransform.getPosition().clone());
                entry.setWorldUuid(store.getExternalData().getWorld().getWorldConfig().getUuid());
                LOGGER.atInfo().log("Teleported pet");
            }
        } else {
            Holder<EntityStore> newEntity = entry.getHolder(true);
            TransformComponent transform = newEntity.getComponent(TransformComponent.getComponentType());
            PetComponent petComponent = newEntity.getComponent(PetComponent.getComponentType());

            if (petComponent == null) {
                LOGGER.atWarning().log("Warning: New holder does not have PetComponent");
            }
            if (transform != null) {
                transform.setPosition(spawnTransform.getPosition().clone());
                entry.setStatus(Status.ALIVE);
                entry.setLastKnownPos(spawnTransform.getPosition().clone());
                entry.setWorldUuid(store.getExternalData().getWorld().getWorldConfig().getUuid());
                LOGGER.atInfo().log("Start spawn pet");

                store.addEntity(newEntity, AddReason.LOAD);
                LOGGER.atInfo().log("Finishing spawn pet");
            } else {
                LOGGER.atWarning().log("Transform is null");
            }
        }

    }

    public static boolean unsummonPet(TrackedPetEntry entry, Store<EntityStore> store) {
        if (!entry.isLoaded()) {
            return false;
        }

        LOGGER.atInfo().log("Starting unsummon pet");
        Ref<EntityStore> existingEntity = store.getExternalData().getRefFromUUID(entry.getUuid());
        if (existingEntity == null || !existingEntity.isValid()) {
            if (entry.isLoaded()) {
                LOGGER.atWarning().log("Pet is not loaded but pet tracker is not in sync, re-syncing");
            }
            return false;
        }
        if (!entry.isLoaded()) {
            LOGGER.atWarning().log("Pet is loaded but pet tracker is not in sync, re-syncing");
        }
        LOGGER.atInfo().log("Executing unsummon pet on " + entry.getUuid() + " which has " + existingEntity.isValid());
        Holder<EntityStore> holder = store.removeEntity(existingEntity, RemoveReason.UNLOAD);
        entry.setHolder(holder);
        entry.clearPosData();
        entry.setStatus(Status.STORED);
        LOGGER.atInfo().log("Finished executing unsummon pet");
        return true;
    }
}
