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
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import io.github.drakonforge.cherishedcreatures.component.PetAttributes;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

// This class is responsible for updating speed multiplier
public class MountHandlingAccelerateGaitSystem extends EntityTickingSystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static float calculateSpeedMultiplier(float dt,
            MountHandlingComponent mountHandlingComponent, PetAttributes petAttributes) {
        PetType petType = mountHandlingComponent.getMountedPetType();
        float targetSpeedMultiplier = mountHandlingComponent.getCurrentGait()
                .getDesiredSpeedMultiplier();
        float currentSpeedMultiplier = mountHandlingComponent.getSpeedMultiplier();
        float gaitAcceleration;
        if (petAttributes.hasAttribute(PetAttributes.MOUNT_GAIT_ACCELERATION)) {
            gaitAcceleration = petAttributes.get(PetAttributes.MOUNT_GAIT_ACCELERATION);
        } else {
            gaitAcceleration = petType.getMountGaitAcceleration().getAverage();
        }
        if (currentSpeedMultiplier < targetSpeedMultiplier) {
            return Math.min(targetSpeedMultiplier, currentSpeedMultiplier + gaitAcceleration * dt);
        } else if (currentSpeedMultiplier > targetSpeedMultiplier) {
            return Math.max(targetSpeedMultiplier, currentSpeedMultiplier - gaitAcceleration * dt);
        }
        return currentSpeedMultiplier;
    }

    @Override
    public void tick(float dt, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        MovementStatesComponent movementStatesComponent = archetypeChunk.getComponent(i,
                MovementStatesComponent.getComponentType());
        MountHandlingComponent mountHandlingComponent = archetypeChunk.getComponent(i,
                MountHandlingComponent.getComponentType());
        PetAttributes petAttributes = archetypeChunk.getComponent(i,
                PetAttributes.getComponentType());
        assert movementStatesComponent != null;
        assert mountHandlingComponent != null;
        assert petAttributes != null;

        float currentSpeedMultiplier = calculateSpeedMultiplier(dt, mountHandlingComponent,
                petAttributes);
        mountHandlingComponent.setSpeedMultiplier(currentSpeedMultiplier);
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(new SystemDependency<>(Order.AFTER, MountHandlingProcessInputSystem.class),
                new SystemDependency<>(Order.AFTER, MountHandlingProcessMovementStates.class));
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(MountHandlingComponent.getComponentType(),
                MovementStatesComponent.getComponentType(), PetAttributes.getComponentType());
    }
}
