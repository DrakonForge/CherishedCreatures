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
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent.MountGait;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

// This class is responsible for updating desired gate based on player input
public class MountHandlingProcessMovementStates extends EntityTickingSystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final float IDLE_THRESHOLD_TO_RESET_MS = 3 * 1000;
    private static final float HELD_SPRINT_THRESHOLD = 2.0f;

    @Override
    public void tick(float dt, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        MovementStatesComponent movementStatesComponent = archetypeChunk.getComponent(i, MovementStatesComponent.getComponentType());
        MountHandlingComponent mountHandlingComponent = archetypeChunk.getComponent(i, MountHandlingComponent.getComponentType());
        assert movementStatesComponent != null;
        assert mountHandlingComponent != null;

        MovementStates movementStates = movementStatesComponent.getMovementStates();
        boolean staminaDepleted = mountHandlingComponent.isStaminaDepleted();

        MountGait prevGait = mountHandlingComponent.getDesiredGait();
        MountGait nextGait = prevGait;
        boolean instant = false;

        long now = System.currentTimeMillis();
        long lastInput = Math.max(mountHandlingComponent.getLastForwardInput(),
                mountHandlingComponent.getLastBackwardInput());
        if (now - lastInput > IDLE_THRESHOLD_TO_RESET_MS) {
            nextGait = MountGait.WALK;
            instant = true;
        } if (staminaDepleted) {
            if (prevGait.ordinal() > MountGait.CANTER.ordinal()) {
                nextGait = MountGait.CANTER;
                instant = true;
            }
        } else if (movementStates.sprinting) {
            if (!mountHandlingComponent.isSprinting()) {
                // When you tap sprint or held sprint time resets, the gait updates
                nextGait = MountGait.toGait(prevGait.ordinal() + 1);
            }
            mountHandlingComponent.incrementHeldSprintTime(dt);
            if (mountHandlingComponent.getHeldSprintTime() >= HELD_SPRINT_THRESHOLD) {
                mountHandlingComponent.resetSprinting();
            }
        } else {
            mountHandlingComponent.resetSprinting();
        }

        if (prevGait != nextGait) {
            LOGGER.atInfo().log("Set gait to " + nextGait.name());
            mountHandlingComponent.setDesiredGait(nextGait, instant);
        }
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(new SystemDependency<>(Order.AFTER, MountHandlingProcessInput.class), new SystemDependency<>(Order.AFTER, MountHandlingUpdateStatsSystem.class));
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(MountHandlingComponent.getComponentType(), MovementStatesComponent.getComponentType());
    }
}
