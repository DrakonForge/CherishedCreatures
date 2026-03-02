package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.asset.PetType.PetFeatureFlag;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingNpcComponent;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import io.github.drakonforge.cherishedcreatures.event.MountNpcEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class AddMountHandlingSystem extends MountNpcEventSystem {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl MountNpcEvent mountNpcEvent) {
        Ref<EntityStore> mountRef = mountNpcEvent.getNewMountRef();
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        PetTypeComponent petTypeComponent = store.getComponent(mountRef,
                PetTypeComponent.getComponentType());
        if (petTypeComponent == null) {
            return;
        }
        PetType petType = petTypeComponent.getPetType();
        if (!petType.hasFeatureFlag(PetFeatureFlag.AdvancedMountHandling)) {
            return;
        }

        MountHandlingComponent mountHandlingComponent = new MountHandlingComponent(petType);
        mountHandlingComponent.setBaseSpeed(petType.getMountBaseSpeed());
        commandBuffer.addComponent(ref, MountHandlingComponent.getComponentType(),
                mountHandlingComponent);
        commandBuffer.ensureComponent(mountRef, MountHandlingNpcComponent.getComponentType());

        MovementManager movementManager = archetypeChunk.getComponent(i, MovementManager.getComponentType());
        PlayerRef playerRef = archetypeChunk.getComponent(i, PlayerRef.getComponentType());
        assert movementManager != null;
        assert playerRef != null;

        // Using this system, sprint only changes the desired gait, not the speed
        movementManager.getSettings().forwardSprintSpeedMultiplier = 1.0f;
        movementManager.getSettings().forwardWalkSpeedMultiplier = 1.0f;
        movementManager.getSettings().backwardWalkSpeedMultiplier = 0.3f;
        movementManager.getSettings().backwardCrouchSpeedMultiplier = 0.3f;
        movementManager.getSettings().backwardRunSpeedMultiplier = 0.3f;
        movementManager.update(playerRef.getPacketHandler());
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(),
                Query.not(MountHandlingComponent.getComponentType()), MovementManager.getComponentType(), PlayerRef.getComponentType());
    }
}
