package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.event.UpdatePetTrackerEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class ResolvePetTrackerChangesSystem extends UpdatePetTrackerEventSystem {

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl UpdatePetTrackerEvent updatePetTrackerEvent) {
        PlayerPetTracker playerPetTracker = archetypeChunk.getComponent(i, PlayerPetTracker.getComponentType());
        assert playerPetTracker != null;
        playerPetTracker.resolveChanges();
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerPetTracker.getComponentType();
    }
}
