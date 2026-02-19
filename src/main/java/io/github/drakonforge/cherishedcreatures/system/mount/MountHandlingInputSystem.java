package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.builtin.mounts.MountSystems;
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
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput.AbsoluteMovement;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput.InputUpdate;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput.RelativeMovement;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput.SetBody;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput.SetClientVelocity;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput.SetHead;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput.SetMovementStates;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput.SetRiderMovementStates;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput.WishMovement;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSystems;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import io.github.drakonforge.cherishedcreatures.component.PlayerNpcMountDetection;
import java.util.List;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class MountHandlingInputSystem extends EntityTickingSystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PlayerNpcMountDetection mountDetection = archetypeChunk.getComponent(i,
                PlayerNpcMountDetection.getComponentType());
        PlayerInput playerInputComponent = archetypeChunk.getComponent(i,
                PlayerInput.getComponentType());
        assert mountDetection != null;
        assert playerInputComponent != null;

        Ref<EntityStore> targetRef = mountDetection.getCurrentMount();
        if (targetRef == null || !targetRef.isValid()) {
            return;
        }
        List<InputUpdate> queue = playerInputComponent.getMovementUpdateQueue();

        if (true) {
            return;
        }

        for (InputUpdate inputUpdate : queue) {
            if (inputUpdate instanceof SetRiderMovementStates(MovementStates states)) {
                LOGGER.atInfo().log("Setting rider movement states");
                MovementStatesComponent movementStatesComponent = archetypeChunk.getComponent(i,
                        MovementStatesComponent.getComponentType());
                assert movementStatesComponent != null;
                movementStatesComponent.setMovementStates(states);
            } else if (!(inputUpdate instanceof WishMovement)) {
                if (inputUpdate instanceof RelativeMovement relative) {
                    relative.apply(commandBuffer, archetypeChunk, i);
                    TransformComponent transform = commandBuffer.getComponent(targetRef,
                            TransformComponent.getComponentType());
                    if (transform != null) {
                        LOGGER.atInfo()
                                .log("Setting relative " + relative.getX() + ", " + relative.getY()
                                        + ", " + relative.getZ());
                        transform.getPosition()
                                .add(relative.getX(), relative.getY(), relative.getZ());
                    }
                } else if (inputUpdate instanceof AbsoluteMovement absolute) {
                    absolute.apply(commandBuffer, archetypeChunk, i);
                    TransformComponent transform = commandBuffer.getComponent(targetRef,
                            TransformComponent.getComponentType());
                    if (transform != null) {
                        LOGGER.atInfo()
                                .log("Setting to pos " + absolute.getX() + ", " + absolute.getY()
                                        + ", " + absolute.getZ());
                        transform.getPosition()
                                .assign(absolute.getX(), absolute.getY(), absolute.getZ());
                    }
                } else if (inputUpdate instanceof SetMovementStates(MovementStates states)) {
                    MovementStatesComponent targetMovementStatesComponent = commandBuffer.getComponent(
                            targetRef, MovementStatesComponent.getComponentType());
                    if (targetMovementStatesComponent != null) {
                        LOGGER.atInfo().log("Setting movement states");
                        targetMovementStatesComponent.setMovementStates(states);
                    }
                } else if (inputUpdate instanceof SetBody body) {
                    body.apply(commandBuffer, archetypeChunk, i);
                    TransformComponent transform = commandBuffer.getComponent(targetRef,
                            TransformComponent.getComponentType());
                    if (transform != null) {
                        LOGGER.atInfo().log("Setting body");
                        transform.getRotation()
                                .assign(body.direction().pitch, body.direction().yaw,
                                        body.direction().roll);
                    }
                } else if (inputUpdate instanceof SetHead head) {
                    LOGGER.atInfo().log("Setting head");
                    head.apply(commandBuffer, archetypeChunk, i);
                } else if (inputUpdate instanceof SetClientVelocity setClientVelocity) {
                    LOGGER.atInfo().log("Setting client velocity");
                    setClientVelocity.apply(commandBuffer, archetypeChunk, i);
                    Velocity velocity = commandBuffer.getComponent(targetRef, Velocity.getComponentType());
                    if (velocity != null) {
                        velocity.set(setClientVelocity.getVelocity());
                    }
                } else {
                    LOGGER.atInfo().log("ignoring non-wish " + inputUpdate.getClass().getName());
                }
            } else {
                LOGGER.atInfo().log("ignoring wish " + inputUpdate.getClass().getName());

            }
        }

        queue.clear();
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(MountHandlingComponent.getComponentType(),
                PlayerNpcMountDetection.getComponentType(),
                MovementStatesComponent.getComponentType(), PlayerInput.getComponentType());
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(new SystemDependency<>(Order.BEFORE, MountSystems.HandleMountInput.class),
                new SystemDependency<>(Order.BEFORE, PlayerSystems.ProcessPlayerInput.class));
    }
}
