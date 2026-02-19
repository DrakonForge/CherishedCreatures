package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementConfig;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.MountStatusMetersComponent;
import io.github.drakonforge.cherishedcreatures.component.MountedActiveComponent;
import io.github.drakonforge.cherishedcreatures.component.PlayerNpcMountDetection;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class UpdateMountStatusMetersSystem extends EntityTickingSystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static void updateStatusMeters(Ref<EntityStore> mountRef,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl MountStatusMetersComponent statusMeters) {
        if (mountRef == null || !mountRef.isValid()) {
            LOGGER.atWarning().log("Mount ref should exist but does not");
            return;
        }

        EntityStatMap mountStatMap = store.getComponent(mountRef, EntityStatMap.getComponentType());
        if (mountStatMap == null) {
            return;
        }

        // TODO: There's a bug when the NPC switches roles due to mounting, health is reset
        EntityStatValue healthValue = mountStatMap.get(DefaultEntityStatTypes.getHealth());
        if (healthValue != null) {
            statusMeters.getHealthMeter().setValue(healthValue.asPercentage());
        }

        EntityStatValue staminaValue = mountStatMap.get(DefaultEntityStatTypes.getStamina());
        if (staminaValue != null) {
            float staminaPercentage = Math.clamp(staminaValue.get() / staminaValue.getMax(), 0.0f, 1.0f);
            statusMeters.getStaminaMeter().setValue(staminaPercentage);
        }
    }

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        MountStatusMetersComponent statusMeters = archetypeChunk.getComponent(i,
                MountStatusMetersComponent.getComponentType());
        PlayerNpcMountDetection mountDetection = archetypeChunk.getComponent(i,
                PlayerNpcMountDetection.getComponentType());
        assert statusMeters != null;
        assert mountDetection != null;

        PlayerRef playerRefRef = archetypeChunk.getComponent(i, PlayerRef.getComponentType());
        Ref<EntityStore> mountRef = mountDetection.getCurrentMount();
        updateStatusMeters(mountRef, store, statusMeters);

        // TODO: This should go into another system
        if (mountRef == null) {
            return;
        }
        EntityStatMap mountStatMap = store.getComponent(mountRef, EntityStatMap.getComponentType());
        if (mountStatMap == null) {
            return;
        }
        EntityStatValue staminaValue = mountStatMap.get(DefaultEntityStatTypes.getStamina());
        if (staminaValue == null) {
            return;
        }
        MovementManager movementManager = archetypeChunk.getComponent(i, MovementManager.getComponentType());
        MovementStatesComponent movementStatesComponent = store.getComponent(mountRef, MovementStatesComponent.getComponentType());
        assert movementManager != null;
        assert movementStatesComponent != null;
        assert playerRefRef != null;


        if (staminaValue.get() <= 0) {
            movementManager.getSettings().forwardSprintSpeedMultiplier = 1;
        } else {
            movementManager.getSettings().forwardSprintSpeedMultiplier = 1.65f; // Hardcoding in the original multiplier
        }
        movementManager.update(playerRefRef.getPacketHandler());

        // Testing some behavior
        // PlayerRef playerRef = archetypeChunk.getComponent(i, PlayerRef.getComponentType());
        // MovementManager manager = archetypeChunk.getComponent(i, MovementManager.getComponentType());
        // MovementStatesComponent movementStatesComponent = archetypeChunk.getComponent(i, MovementStatesComponent.getComponentType());
        // assert movementStatesComponent != null;
        // assert playerRef != null;
        // assert manager != null;
        // if (movementStatesComponent.getMovementStates().walking) {
        //     LOGGER.atInfo().log("WALKING");
        //     manager.getSettings().baseSpeed = 0;
        // } else if (movementStatesComponent.getMovementStates().sprinting) {
        //     LOGGER.atInfo().log("SPRINTING");
        //     manager.getSettings().baseSpeed = 20;
        // } else {
        //     LOGGER.atInfo().log("RUNNING");
        //     manager.getSettings().baseSpeed = 10;
        // }
        // MovementStatesComponent movementStatesComponent1 = archetypeChunk.getComponent(i, MovementStatesComponent.getComponentType());
        // assert  movementStatesComponent1 != null;
        // if (movementStatesComponent1.getMovementStates().walking) {
        //     LOGGER.atInfo().log("WALKING 2");
        //     manager.getSettings().baseSpeed = 0;
        // } else if (movementStatesComponent1.getMovementStates().sprinting) {
        //     LOGGER.atInfo().log("SPRINTING 2");
        //     manager.getSettings().baseSpeed = 20;
        // } else {
        //     LOGGER.atInfo().log("RUNNING 2");
        //     manager.getSettings().baseSpeed = 10;
        // }
        //
        // manager.getSettings().forwardSprintSpeedMultiplier = 1;
        // manager.update(playerRef.getPacketHandler());
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(MountedActiveComponent.getComponentType(),
                MountStatusMetersComponent.getComponentType(),
                PlayerNpcMountDetection.getComponentType());
    }
}
