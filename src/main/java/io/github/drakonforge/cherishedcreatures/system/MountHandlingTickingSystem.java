package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class MountHandlingTickingSystem extends EntityTickingSystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        EntityStatMap entityStatMap = archetypeChunk.getComponent(i, EntityStatMap.getComponentType());
        assert entityStatMap != null;
        EntityStatValue staminaValue = entityStatMap.get(DefaultEntityStatTypes.getStamina());
        if (staminaValue != null) {
            float value = staminaValue.get();
            LOGGER.atInfo().log("Stamina: " + value);
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(MountHandlingComponent.getComponentType(), EntityStatMap.getComponentType());
    }
}
