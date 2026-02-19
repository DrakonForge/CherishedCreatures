package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.MountStatusMetersComponent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class EnsureMountStatusMetersSystem extends RefSystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), PlayerRef.getComponentType());
    }

    @Override
    public void onEntityAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl AddReason addReason,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        MountStatusMetersComponent mountStatusMetersComponent = commandBuffer.ensureAndGetComponent(
                ref, MountStatusMetersComponent.getComponentType());
        PlayerRef playerRef = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
        assert playerRef != null;
        World world = store.getExternalData().getWorld();

        // Temporary workaround because HyUI assigns unique HUD IDs based on milliseconds
        world.execute(() -> {
            mountStatusMetersComponent.getHealthMeter().addHud(playerRef);
            try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
                scheduler.schedule(() -> world.execute(() -> {
                    mountStatusMetersComponent.getStaminaMeter().addHud(playerRef);
                }), 5, TimeUnit.MILLISECONDS);
            }
        });

    }

    @Override
    public void onEntityRemove(@NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

    }
}
