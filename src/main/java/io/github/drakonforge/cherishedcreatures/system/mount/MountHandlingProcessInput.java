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
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput.InputUpdate;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput.SetClientVelocity;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSystems.ProcessPlayerInput;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import java.util.List;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

// Based on ProcessPlayerInputSystem
// This doesn't actually replace the system (which we could do by clearing the queue and applying it ourselves
// We're just listening in
public class MountHandlingProcessInput extends EntityTickingSystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final double DIRECTION_THRESHOLD = 0.9;

    @Override
    public void tick(float v, int index, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PlayerInput playerInputComponent = archetypeChunk.getComponent(index,
                PlayerInput.getComponentType());
        TransformComponent transformComponent = archetypeChunk.getComponent(index,
                TransformComponent.getComponentType());
        MountHandlingComponent mountHandlingComponent = archetypeChunk.getComponent(index,
                MountHandlingComponent.getComponentType());
        assert playerInputComponent != null;
        assert transformComponent != null;
        assert mountHandlingComponent != null;

        List<InputUpdate> movementUpdateQueue = playerInputComponent.getMovementUpdateQueue();

        Transform transform = transformComponent.getTransform();
        Vector3d direction = transform.getDirection().clone().normalize();
        double directionX = direction.getX();
        double directionZ = direction.getZ();
        for (PlayerInput.InputUpdate entry : movementUpdateQueue) {
            if (entry instanceof SetClientVelocity setClientVelocity) {
                Vector3d velocity = setClientVelocity.getVelocity().clone().normalize();
                double velocityX = velocity.getX();
                double velocityZ = velocity.getZ();
                double dot = directionX * velocityX + directionZ * velocityZ;
                long now = System.currentTimeMillis();
                if (dot >= DIRECTION_THRESHOLD) {
                    mountHandlingComponent.setLastForwardInput(now);
                } else if (dot <= -DIRECTION_THRESHOLD) {
                    mountHandlingComponent.setLastBackwardInput(now);
                }
            }
        }
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(new SystemDependency<>(Order.BEFORE, ProcessPlayerInput.class));
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(MountHandlingComponent.getComponentType(), PlayerInput.getComponentType(),
                TransformComponent.getComponentType());
    }
}
