package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import io.github.drakonforge.cherishedcreatures.event.DismountNpcEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class RemoveMountHandlingSystem extends EntityEventSystem<EntityStore, DismountNpcEvent> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public RemoveMountHandlingSystem() {
        super(DismountNpcEvent.class);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), MountHandlingComponent.getComponentType());
    }

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl DismountNpcEvent dismountNpcEvent) {
        LOGGER.atInfo().log("DISMOUNTED NPC");
        Ref<EntityStore> playerRef = archetypeChunk.getReferenceTo(i);
        commandBuffer.tryRemoveComponent(playerRef, MountHandlingComponent.getComponentType());

    }
}
