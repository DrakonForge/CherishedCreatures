package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.stamina.SprintStaminaRegenDelay;
import com.hypixel.hytale.server.core.modules.entity.stamina.StaminaModule;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import io.github.drakonforge.cherishedcreatures.component.MountedActiveComponent;
import io.github.drakonforge.cherishedcreatures.component.PlayerNpcMountDetection;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SetMountHandlingStaminaDelaySystem extends EntityTickingSystem<EntityStore> {
    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PlayerNpcMountDetection mountDetection = archetypeChunk.getComponent(i, PlayerNpcMountDetection.getComponentType());
        assert mountDetection != null;
        Ref<EntityStore> mountRef = mountDetection.getCurrentMount();
        if (mountRef == null) {
            return;
        }
        MovementStatesComponent movementStates = store.getComponent(mountRef, MovementStatesComponent.getComponentType());
        EntityStatMap entityStatMap = store.getComponent(mountRef, EntityStatMap.getComponentType());
        if (movementStates == null || entityStatMap == null) {
            return;
        }

        if (movementStates.getMovementStates().sprinting) {
            SprintStaminaRegenDelay regenDelay = store.getResource(StaminaModule.get()
                    .getSprintRegenDelayResourceType());
            EntityStatValue statValue = entityStatMap.get(regenDelay.getIndex());
            if (statValue != null && statValue.get() <= regenDelay.getValue()) {
                return;
            }

            entityStatMap.setStatValue(regenDelay.getIndex(), regenDelay.getValue());
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(PlayerNpcMountDetection.getComponentType(), MountedActiveComponent.getComponentType(),
                MountHandlingComponent.getComponentType());
    }
}
