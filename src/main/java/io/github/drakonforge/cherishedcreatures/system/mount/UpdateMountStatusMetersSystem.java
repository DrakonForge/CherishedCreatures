package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
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
            statusMeters.getStaminaMeter().setValue(staminaValue.asPercentage());
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

        Ref<EntityStore> mountRef = mountDetection.getCurrentMount();
        updateStatusMeters(mountRef, store, statusMeters);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(MountedActiveComponent.getComponentType(),
                MountStatusMetersComponent.getComponentType(),
                PlayerNpcMountDetection.getComponentType());
    }
}
