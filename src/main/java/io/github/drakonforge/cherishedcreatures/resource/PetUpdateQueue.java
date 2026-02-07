package io.github.drakonforge.cherishedcreatures.resource;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.event.ReceivePetUpdatesEvent;
import io.github.drakonforge.cherishedcreatures.update.PetUpdate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PetUpdateQueue implements Resource<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static final BuilderCodec<PetUpdateQueue> CODEC = BuilderCodec.builder(
                    PetUpdateQueue.class, PetUpdateQueue::new)
            .append(new KeyedCodec<>("QueuedUpdates",
                            new ArrayCodec<>(PetUpdate.CODEC, PetUpdate[]::new)),
                    PetUpdateQueue::loadQueuedUpdates, PetUpdateQueue::saveQueuedUpdates)
            .add()
            .build();

    private List<PetUpdate> queuedUpdates = new ArrayList<>();

    public void queueUpdate(PetUpdate petUpdate) {
        queuedUpdates.add(petUpdate);
    }

    public void deliverUpdatesForPlayer(Store<EntityStore> store, Ref<EntityStore> playerRef) {
        UUIDComponent uuidComponent = store.getComponent(playerRef, UUIDComponent.getComponentType());
        if (uuidComponent == null) {
            LOGGER.atWarning().log("Missing UUID for player");
            return;
        }
        UUID uuid = uuidComponent.getUuid();
        LOGGER.atInfo().log("Resolving pet updates for player " + uuid);
        List<PetUpdate> updatesToSend = new ArrayList<>();
        List<PetUpdate> updatesResolved = new ArrayList<>();
        for (PetUpdate update : queuedUpdates) {
            if (!update.isDeliveredToOwner() && update.getOwnerUuid().equals(uuid)) {
                updatesToSend.add(update);
                update.markDeliveredToOwner();
                if (update.completed()) {
                    updatesResolved.add(update);
                }
            }
        }
        store.invoke(playerRef, new ReceivePetUpdatesEvent(updatesToSend, false));
        queuedUpdates.removeAll(updatesResolved);
    }

    public void deliverUpdatesForPet(Store<EntityStore> store, Ref<EntityStore> entityRef) {
        UUIDComponent uuidComponent = store.getComponent(entityRef, UUIDComponent.getComponentType());
        if (uuidComponent == null) {
            LOGGER.atWarning().log("Missing UUID for pet");
            return;
        }
        UUID uuid = uuidComponent.getUuid();
        LOGGER.atInfo().log("Resolving pet updates for pet " + uuid);
        List<PetUpdate> updatesToSend = new ArrayList<>();
        List<PetUpdate> updatesResolved = new ArrayList<>();
        for (PetUpdate update : queuedUpdates) {
            if (!update.isDeliveredToPet() && update.getPetUuid().equals(uuid)) {
                updatesToSend.add(update);
                update.markDeliveredToPet();
                if (update.completed()) {
                    updatesResolved.add(update);
                }
            }
        }
        store.invoke(entityRef, new ReceivePetUpdatesEvent(updatesToSend, false));
        queuedUpdates.removeAll(updatesResolved);
    }

    private void loadQueuedUpdates(PetUpdate[] array) {
        queuedUpdates = Arrays.stream(array).toList();
    }

    private PetUpdate[] saveQueuedUpdates() {
        return queuedUpdates.toArray(new PetUpdate[0]);
    }

    @NullableDecl
    @Override
    public Resource<EntityStore> clone() {
        PetUpdateQueue clone = new PetUpdateQueue();
        return clone;
    }
}
