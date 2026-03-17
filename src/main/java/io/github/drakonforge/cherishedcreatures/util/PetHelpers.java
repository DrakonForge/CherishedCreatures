package io.github.drakonforge.cherishedcreatures.util;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.components.messaging.BeaconSupport;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.asset.PetType.AbandonBehavior;
import io.github.drakonforge.cherishedcreatures.asset.PetType.PetFeatureFlag;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry.Status;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class PetHelpers {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final double LONG_WHISTLE_RANGE = 200.0;
    private static final float BEACON_EXPIRATION_TIME = 1.0f;

    public enum TameResult {
        SUCCESS,
        FAIL_MISSING_COMPONENTS,
        FAIL_NOT_TAMEABLE,
        FAIL_ALREADY_TAMED_BY_OTHERS,
        FAIL_ALREADY_TAMED_BY_SELF
    }

    public enum UntameResult {
        SUCCESS,
        FAIL_MISSING_COMPONENTS,
        FAIL_MUST_BE_SPAWNED,
        FAIL_ALREADY_DEAD,
        FAIL_REMOVE_FROM_TRACKER,
    }

    public static TameResult attemptTame(@Nonnull Store<EntityStore> store,  @Nonnull Ref<EntityStore> playerRef, @Nonnull Ref<EntityStore> petRef) {
        PlayerPetTracker playerPetTracker = store.getComponent(playerRef, PlayerPetTracker.getComponentType());
        UUIDComponent uuidComponent = store.getComponent(playerRef, UUIDComponent.getComponentType());
        if (playerPetTracker == null || uuidComponent == null) {
            return TameResult.FAIL_MISSING_COMPONENTS;
        }

        PetTypeComponent petTypeComponent = store.getComponent(petRef, PetTypeComponent.getComponentType());
        if (petTypeComponent == null || !petTypeComponent.isTameable()) {
            return TameResult.FAIL_NOT_TAMEABLE;
        }
        PetComponent existingPetComponent = store.getComponent(petRef, PetComponent.getComponentType());
        if (existingPetComponent != null) {
            if (uuidComponent.getUuid().equals(existingPetComponent.getOwnerUuid())) {
                return TameResult.FAIL_ALREADY_TAMED_BY_SELF;
            }
            return TameResult.FAIL_ALREADY_TAMED_BY_OTHERS;
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

    public static UntameResult attemptUntame(Store<EntityStore> store, PlayerPetTracker petTracker, TrackedPetEntry entry) {
        PetType petType = entry.getPetType();
        AbandonBehavior abandonBehavior = petType.getAbandonBehavior();

        if (entry.getStatus() == Status.DEAD) {
            return UntameResult.FAIL_ALREADY_DEAD;
        }

        if (abandonBehavior == AbandonBehavior.UntameIfSpawned && !entry.isLoaded()) {
            return UntameResult.FAIL_MUST_BE_SPAWNED;
        }

        // Remove from tracker first
        boolean success = petTracker.removePetEntry(entry.getUuid());
        if (!success) {
            return UntameResult.FAIL_REMOVE_FROM_TRACKER;
        }

        // Then the components of the active entity
        Ref<EntityStore> ref = entry.getEntityRef();
        if (entry.isLoaded() && ref != null) {
            World world = store.getExternalData().getWorld();
            world.execute(() -> {
                if (abandonBehavior == AbandonBehavior.Despawn) {
                    store.removeEntity(ref, RemoveReason.REMOVE);
                } else if (abandonBehavior == AbandonBehavior.UntameIfSpawned) {
                    store.removeComponent(ref, PetComponent.getComponentType());
                }
            });
        }



       return UntameResult.SUCCESS;
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
            EntityStatMap entityStatMap = newEntity.getComponent(EntityStatMap.getComponentType());
            if (petComponent == null || transform == null || entityStatMap == null) {
                LOGGER.atWarning().log("Missing components, aborting summonPet call");
                return;
            }

            transform.setPosition(spawnTransform.getPosition().clone());
            entry.setStatus(Status.ALIVE);
            entry.setLastKnownPos(spawnTransform.getPosition().clone());
            entry.setWorldUuid(store.getExternalData().getWorld().getWorldConfig().getUuid());

            // Thanks to BalancingInitialisationSystem, health is set to max every spawn
            // This fixes that
            EntityStatValue oldHealthStat = entityStatMap.get(DefaultEntityStatTypes.getHealth());
            float oldValue;
            if (oldHealthStat == null) {
                oldValue = -1.0f;
            } else {
                oldValue = oldHealthStat.get();
            }

            Ref<EntityStore> ref = store.addEntity(newEntity, AddReason.LOAD);
            if (ref != null && !entry.getPetType().hasFeatureFlag(PetFeatureFlag.HealsOnSpawn)) {
                EntityStatMap newStatMap = store.getComponent(ref, EntityStatMap.getComponentType());
                if (newStatMap != null) {
                    newStatMap.setStatValue(DefaultEntityStatTypes.getHealth(), oldValue);
                }
            }
            LOGGER.atInfo().log("Spawned pet");
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

    public static boolean renamePet(TrackedPetEntry entry, Store<EntityStore> store, String newName) {
        Ref<EntityStore> ref = entry.getEntityRef();
        if (entry.isLoaded() && ref != null) {
            PetComponent petComponent = store.getComponent(ref, PetComponent.getComponentType());
            if (petComponent != null) {
                petComponent.setPetName(newName);
            }
            entry.saveEntityFromRef(store, ref);
            return true;
        }

        Holder<EntityStore> holder = entry.getHolder(false);
        PetComponent petComponent = holder.getComponent(PetComponent.getComponentType());
        if (petComponent != null) {
            petComponent.setPetName(newName);
        }
        return true;
    }

    public static void doShortWhistle(ComponentAccessor<EntityStore> store, Ref<EntityStore> ref) {
        Ref<EntityStore> nearest = TargetLookHelpers.getEntityNearestToCrosshair(ref, 64.0, Math.toRadians(15.0), store);
        if (nearest != null) {
            DisplayNameComponent nearestDisplayName = store.getComponent(nearest, DisplayNameComponent.getComponentType());
            TransformComponent nearestTransform = store.getComponent(nearest, TransformComponent.getComponentType());

            if (nearestDisplayName == null || nearestTransform == null) {
                LOGGER.atWarning().log("Missing components for whistle target");
                return;
            }
            String mobName = nearestDisplayName.getDisplayName() != null ? nearestDisplayName.getDisplayName().getAnsiMessage() : null;
            LOGGER.atInfo().log("Whistled at " + mobName);
            List<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
            SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = store.getResource(
                    EntityModule.get().getPlayerSpatialResourceType());
            playerSpatialResource.getSpatialStructure().collect(nearestTransform.getPosition(),
                    75.0F, results);
            ParticleUtil.spawnParticleEffect("Angry", nearestTransform.getPosition().clone().add(new Vector3d(0, 2, 0)), nearestTransform.getRotation(), results, store);
        } else {
            LOGGER.atInfo().log("No whistle target");
        }
    }

    public static void doLongWhistle(ComponentAccessor<EntityStore> store, Ref<EntityStore> ref) {
        PlayerPetTracker petTracker = store.getComponent(ref, PlayerPetTracker.getComponentType());
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (petTracker == null || transformComponent == null) {
            LOGGER.atWarning().log("Missing components on whistling player");
            return;
        }

        for (TrackedPetEntry petEntry : petTracker.getPetEntries()) {
            Ref<EntityStore> petRef = petEntry.getEntityRef();
            if (petRef == null || !petRef.isValid()) {
                continue;
            }
            TransformComponent petTransform = store.getComponent(petRef, TransformComponent.getComponentType());
            BeaconSupport beaconSupport = store.getComponent(petRef, BeaconSupport.getComponentType());
            if (petTransform == null || beaconSupport == null) {
                continue;
            }
            double distSq = petTransform.getPosition().distanceSquaredTo(transformComponent.getPosition());
            if (distSq <= LONG_WHISTLE_RANGE * LONG_WHISTLE_RANGE) {
                beaconSupport.postMessage("Whistle_Recall", ref, BEACON_EXPIRATION_TIME);
            }
        }


    }

    public enum TransferPetResult {
        SUCCESS,
        FAIL_INVALID_PLAYER, // Possibly due to one of the UUIDs never having been online
        FAIL_INVALID_PET, // Original owner does not own that pet
        FAIL_MISSING_COMPONENTS
    }

    // If a transferred pet is in an unloaded chunk and spawns in, it should detect the PetComponent no longer matches and then despawn immediately
    public static TransferPetResult transferPet(Store<EntityStore> store, UUID originalOwnerId, UUID newOwnerId, UUID petId) {
        PlayerPetTracker originalPetTracker = OfflinePlayerHelpers.getComponent(store, originalOwnerId, PlayerPetTracker.getComponentType());
        PlayerPetTracker newPetTracker = OfflinePlayerHelpers.getComponent(store, newOwnerId, PlayerPetTracker.getComponentType());
        if (originalPetTracker == null || newPetTracker == null) {
            return TransferPetResult.FAIL_INVALID_PLAYER;
        }

        TrackedPetEntry entryToTransfer = originalPetTracker.getPetEntry(petId);
        if (entryToTransfer == null) {
            return TransferPetResult.FAIL_INVALID_PET;
        }
        // TODO: Check for if the original owner and new owner are the same and prevent transfer if so
        originalPetTracker.removePetEntry(petId);
        newPetTracker.addPetEntry(entryToTransfer);

        Ref<EntityStore> petRef = entryToTransfer.getEntityRef();
        if (petRef != null && petRef.isValid()) {
            // Pet is loaded, modify it directly
            PetComponent petComponent = store.getComponent(petRef, PetComponent.getComponentType());
            if (petComponent == null) {
                return TransferPetResult.FAIL_MISSING_COMPONENTS;
            }
            petComponent.setOwnerUuid(newOwnerId);
            entryToTransfer.saveEntityFromRef(store, petRef);
        } else {
            // Pet is not loaded, modify the holder
            Holder<EntityStore> holder = entryToTransfer.getHolder(false);
            PetComponent petComponent = holder.getComponent(PetComponent.getComponentType());
            if (petComponent == null) {
                return TransferPetResult.FAIL_MISSING_COMPONENTS;
            }
            petComponent.setOwnerUuid(newOwnerId);
        }
        return TransferPetResult.SUCCESS;

    }
}
