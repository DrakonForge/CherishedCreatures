package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent.MountGait;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingNpcComponent;
import java.util.Objects;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class MountHandlingUpdateAnimationsSystem extends EntityTickingSystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        NPCEntity npcEntity = archetypeChunk.getComponent(i,
                Objects.requireNonNull(NPCEntity.getComponentType()));
        MountHandlingNpcComponent mountHandlingNpcComponent = archetypeChunk.getComponent(i, MountHandlingNpcComponent.getComponentType());
        TransformComponent transformComponent = archetypeChunk.getComponent(i, TransformComponent.getComponentType());
        MovementStatesComponent movementStatesComponent = archetypeChunk.getComponent(i, MovementStatesComponent.getComponentType());
        assert mountHandlingNpcComponent != null;
        assert npcEntity != null;
        assert movementStatesComponent != null;
        assert transformComponent != null;

        // Get dot product of facing direction and velocity.
        // Value near 1 indicates forward movement, near -1 indicates backward movement.
        Transform transform = transformComponent.getTransform();
        Vector3d direction = transform.getDirection().clone().normalize();
        Vector3d velocity = mountHandlingNpcComponent.getVelocity().clone().normalize();
        double dot = direction.dot(velocity);

        // TODO: Move this out to a data-driven system. Backward animation, then animations per gait
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        MountGait currentGait = mountHandlingNpcComponent.getCurrentGait();
        if (movementStatesComponent.getMovementStates().idle) {
            npcEntity.playAnimation(ref, AnimationSlot.Movement, null, store);
        } else if(dot < 0) {
            npcEntity.playAnimation(ref, AnimationSlot.Movement, "WalkBackward", store);
        } else if (currentGait.ordinal() >= MountGait.Canter.ordinal()) {
            npcEntity.playAnimation(ref, AnimationSlot.Movement, "Run", store);
        } else {
            npcEntity.playAnimation(ref, AnimationSlot.Movement, "Walk", store);
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
        return Query.and(MountHandlingNpcComponent.getComponentType(), NPCEntity.getComponentType(), MovementStatesComponent.getComponentType());
    }
}
