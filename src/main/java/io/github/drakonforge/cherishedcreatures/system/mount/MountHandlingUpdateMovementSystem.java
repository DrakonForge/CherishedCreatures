package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class MountHandlingUpdateMovementSystem extends EntityTickingSystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        MovementManager movementManager = archetypeChunk.getComponent(i, MovementManager.getComponentType());
        MountHandlingComponent mountHandlingComponent = archetypeChunk.getComponent(i, MountHandlingComponent.getComponentType());
        assert movementManager != null;
        assert mountHandlingComponent != null;
        boolean anyChange = false;

        float baseSpeed = mountHandlingComponent.getBaseSpeed();
        // TODO: Epsilon check for close enough? If it reduces bandwidth usage
        // TODO: Can consider throttling it as well but I don't think it's an issue
        MovementSettings movementSettings = movementManager.getSettings();
        if (baseSpeed != mountHandlingComponent.getLastSentBaseSpeed()) {
            mountHandlingComponent.setLastSentBaseSpeed(baseSpeed);
            movementSettings.baseSpeed = baseSpeed;
            anyChange = true;
        }

        float speedMultiplier = mountHandlingComponent.getSpeedMultiplier();
        if (speedMultiplier != mountHandlingComponent.getLastSentSpeedMultiplier()) {
            mountHandlingComponent.setLastSentSpeedMultiplier(speedMultiplier);
            movementSettings.forwardRunSpeedMultiplier = speedMultiplier;
            movementSettings.forwardWalkSpeedMultiplier = speedMultiplier;
            movementSettings.forwardSprintSpeedMultiplier = speedMultiplier;
            movementSettings.forwardCrouchSpeedMultiplier = speedMultiplier;
            anyChange = true;
        }

        if (anyChange) {
            PlayerRef playerRef = archetypeChunk.getComponent(i, PlayerRef.getComponentType());
            assert playerRef != null;
            movementManager.update(playerRef.getPacketHandler());
        }
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(new SystemDependency<>(Order.AFTER, MountHandlingAccelerateGaitSystem.class));
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(MountHandlingComponent.getComponentType(), MovementManager.getComponentType(),
                PlayerRef.getComponentType());
    }
}
