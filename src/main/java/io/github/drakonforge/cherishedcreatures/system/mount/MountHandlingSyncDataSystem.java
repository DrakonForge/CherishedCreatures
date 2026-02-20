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
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingNpcComponent;
import io.github.drakonforge.cherishedcreatures.component.PlayerNpcMountDetection;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class MountHandlingSyncDataSystem extends EntityTickingSystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        MountHandlingComponent mountHandlingComponent = archetypeChunk.getComponent(i,
                MountHandlingComponent.getComponentType());
        PlayerNpcMountDetection mountDetection = archetypeChunk.getComponent(i,
                PlayerNpcMountDetection.getComponentType());
        Velocity velocity = archetypeChunk.getComponent(i, Velocity.getComponentType());
        assert mountHandlingComponent != null;
        assert mountDetection != null;
        assert velocity != null;

        Ref<EntityStore> mountRef = mountDetection.getCurrentMount();
        if (mountRef == null || !mountRef.isValid()) {
            LOGGER.atWarning().log("Mount ref should not be null");
            return;
        }

        MountHandlingNpcComponent mountHandlingNpcComponent = store.getComponent(mountRef,
                MountHandlingNpcComponent.getComponentType());
        if (mountHandlingNpcComponent == null) {
            LOGGER.atWarning().log("MountHandlingNpcComponent should exist on mount but does not");
            return;
        }
        mountHandlingNpcComponent.setCurrentGait(mountHandlingComponent.getCurrentGait());
        mountHandlingNpcComponent.setVelocity(velocity.getClientVelocity().clone());
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(
                new SystemDependency<>(Order.AFTER, MountHandlingProcessMovementStates.class));
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(MountHandlingComponent.getComponentType(),
                PlayerNpcMountDetection.getComponentType(), Velocity.getComponentType());
    }
}
