package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

// Grabs the calculated speed multiplier, base speed, and any other modifiers, and sets mountHandlingComponent to have the current speed
public class MountHandlingCalculateSpeedSystem extends EntityTickingSystem<EntityStore> {

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        MountHandlingComponent mountHandlingComponent = archetypeChunk.getComponent(i, MountHandlingComponent.getComponentType());
        assert mountHandlingComponent != null;

        // TODO: Apply speed increase per level
        float speed = mountHandlingComponent.getMountedPetType().getBaseSpeed() * mountHandlingComponent.getSpeedMultiplier();
        mountHandlingComponent.setCurrentSpeed(speed);
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(new SystemDependency<>(Order.AFTER, MountHandlingAccelerateGaitSystem.class));
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(MountHandlingComponent.getComponentType(),
                PlayerRef.getComponentType());
    }
}
