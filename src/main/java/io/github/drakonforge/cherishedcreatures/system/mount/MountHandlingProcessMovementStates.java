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
import com.hypixel.hytale.server.core.Message;
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
    private static final long IDLE_THRESHOLD_TO_RESET_MS = 500;
    private static final float HELD_SPRINT_THRESHOLD = 0.35f;
    private static final float TAP_SPRINT_THRESHOLD = 0.2f;
    private static final long BRAKE_THRESHOLD_MS = 200;

    @Override
    public void tick(float dt, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        MovementStatesComponent movementStatesComponent = archetypeChunk.getComponent(i,
                MovementStatesComponent.getComponentType());
        MountHandlingComponent mountHandlingComponent = archetypeChunk.getComponent(i,
                MountHandlingComponent.getComponentType());
        assert movementStatesComponent != null;
        assert mountHandlingComponent != null;

        MovementStates movementStates = movementStatesComponent.getMovementStates();
        boolean staminaDepleted = mountHandlingComponent.isStaminaDepleted();

        MountGait prevGait = mountHandlingComponent.getCurrentGait();
        MountGait nextGait = prevGait;
        boolean instant = false;

        long now = System.currentTimeMillis();
        long lastForwardInput = mountHandlingComponent.getLastForwardInput();
        long lastBackwardInput = mountHandlingComponent.getLastBackwardInput();
        float heldSprintTime = mountHandlingComponent.getHeldSprintTime();
        if (now - lastForwardInput > IDLE_THRESHOLD_TO_RESET_MS || (now - lastBackwardInput <= BRAKE_THRESHOLD_MS && lastBackwardInput < lastForwardInput)) {
            // Mount was inactive for too long, reset gait to WALK
            nextGait = MountGait.Walk;
            instant = true;
        } else if (staminaDepleted) {
            if (prevGait.ordinal() > MountGait.Canter.ordinal()) {
                // Rest - Stamina is depleted, cannot maintain anything higher than a CANTER
                nextGait = MountGait.Canter;
                instant = true;
            }
        } else if (movementStates.sprinting) {
            if (heldSprintTime >= TAP_SPRINT_THRESHOLD) {
                // Giddy-up - For responsiveness, immediately increase the canter to at least TROT when tapped
                nextGait = MountGait.toGait(Math.max(prevGait.ordinal(), MountGait.Trot.ordinal()));
            }
            if (heldSprintTime >= HELD_SPRINT_THRESHOLD && prevGait.ordinal() < MountGait.Gallop.ordinal()) {
                // Natural Increase - When you hold sprint for a certain amount of time, gait increases up to GALLOP
                // Maintain FULL_GALLOP if already there
                // Need to tap to reach FULL_GALLOP
                mountHandlingComponent.resetSprinting();
                nextGait = MountGait.toGait(prevGait.ordinal() + 1);
            }
            mountHandlingComponent.incrementHeldSprintTime(dt);
        } else {
            if (0.0f < heldSprintTime && heldSprintTime <= TAP_SPRINT_THRESHOLD) {
                // Faster! - When you tap sprint and release quickly, gait increases
                // TODO: Don't double-dip with Giddy-up
                nextGait = MountGait.toGait(prevGait.ordinal() + 1);
            }
            mountHandlingComponent.resetSprinting();
        }

        if (prevGait != nextGait) {
            LOGGER.atInfo().log("Set gait to " + nextGait.name());
            store.getExternalData().getWorld().sendMessage(Message.raw("Set gait to " + nextGait.name()));
            mountHandlingComponent.setCurrentGait(nextGait, instant);
        }
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(new SystemDependency<>(Order.AFTER, MountHandlingProcessInputSystem.class),
                new SystemDependency<>(Order.AFTER, MountHandlingUpdateStatsSystem.class));
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(MountHandlingComponent.getComponentType(),
                MovementStatesComponent.getComponentType());
    }
}
