package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent.MountGait;
import io.github.drakonforge.cherishedcreatures.component.PlayerNpcMountDetection;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class MountHandlingTickingSystem extends EntityTickingSystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PlayerRef playerRef = archetypeChunk.getComponent(i, PlayerRef.getComponentType());
        MovementManager movementManager = archetypeChunk.getComponent(i, MovementManager.getComponentType());
        MovementStatesComponent movementStatesComponent = archetypeChunk.getComponent(i, MovementStatesComponent.getComponentType());
        MountHandlingComponent mountHandlingComponent = archetypeChunk.getComponent(i, MountHandlingComponent.getComponentType());
        assert playerRef != null;
        assert movementManager != null;
        assert movementStatesComponent != null;
        assert mountHandlingComponent != null;

        MovementStates movementStates = movementStatesComponent.getMovementStates();
        // LOGGER.atInfo().log("IDLE " + movementStates.idle + ", SPRINT " + movementStates.sprinting);
        if (mountHandlingComponent.isStaminaDepleted()) {
            mountHandlingComponent.setDesiredGait(MountGait.WALK);
        } else if (movementStates.sprinting) {
            mountHandlingComponent.setDesiredGait(MountGait.GALLOP);
        }
        // Otherwise, desired gait does not change
        // LOGGER.atInfo().log("Desired gait: " + mountHandlingComponent.getDesiredGait().name());

        movementManager.update(playerRef.getPacketHandler());
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(MountHandlingComponent.getComponentType(), PlayerRef.getComponentType(), MovementManager.getComponentType(), MovementStatesComponent.getComponentType());
    }
}
