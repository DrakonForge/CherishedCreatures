package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.event.ReceivePetUpdatesEvent;
import io.github.drakonforge.cherishedcreatures.update.PetUpdate;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class ResolvePetUpdatesOwnerSystem extends EntityEventSystem<EntityStore, ReceivePetUpdatesEvent> {

    public ResolvePetUpdatesOwnerSystem() {
        super(ReceivePetUpdatesEvent.class);
    }

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl ReceivePetUpdatesEvent receivePetUpdatesEvent) {
        for (PetUpdate update : receivePetUpdatesEvent.getPetUpdates()) {
            // TODO: Handle update for player
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }
}
