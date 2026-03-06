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
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset.AnimationSet;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent.MountGait;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingNpcComponent;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class MountHandlingUpdateAnimationsSystem extends EntityTickingSystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static String getAnimationWithFallback(Map<String, AnimationSet> animationStepMap, String animationId, String fallbackAnimationId) {
        if (!animationStepMap.containsKey(animationId)) {
            LOGGER.atInfo().log("Using fallback for " + animationId);
            return fallbackAnimationId;
        }
        return animationId;
    }

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        NPCEntity npcEntity = archetypeChunk.getComponent(i,
                Objects.requireNonNull(NPCEntity.getComponentType()));
        MountHandlingNpcComponent mountHandlingNpcComponent = archetypeChunk.getComponent(i, MountHandlingNpcComponent.getComponentType());
        TransformComponent transformComponent = archetypeChunk.getComponent(i, TransformComponent.getComponentType());
        MovementStatesComponent movementStatesComponent = archetypeChunk.getComponent(i, MovementStatesComponent.getComponentType());
        ModelComponent modelComponent = archetypeChunk.getComponent(i, ModelComponent.getComponentType());
        assert mountHandlingNpcComponent != null;
        assert npcEntity != null;
        assert movementStatesComponent != null;
        assert transformComponent != null;
        assert modelComponent != null;

        // Get dot product of facing direction and velocity.
        // Value near 1 indicates forward movement, near -1 indicates backward movement.
        Transform transform = transformComponent.getTransform();
        Vector3d direction = transform.getDirection().clone().normalize();
        Vector3d velocity = mountHandlingNpcComponent.getVelocity().clone().normalize();
        double dot = direction.dot(velocity);
        Map<String, AnimationSet> animationStepMap = modelComponent.getModel().getAnimationSetMap();

        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        MountGait currentGait = mountHandlingNpcComponent.getCurrentGait();
        String animationId;
        if (movementStatesComponent.getMovementStates().idle) {
            animationId = null;
        } else if(dot < 0) {
            animationId = "WalkBackward";
        } else {
            animationId = getAnimationWithFallback(animationStepMap, currentGait.getAnimationId(),
                    currentGait.getFallbackAnimationId());
        }
        npcEntity.playAnimation(ref, AnimationSlot.Movement, animationId, store);
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
