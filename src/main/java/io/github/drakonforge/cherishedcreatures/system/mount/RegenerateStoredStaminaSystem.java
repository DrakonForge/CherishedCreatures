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
import io.github.drakonforge.cherishedcreatures.component.PlayerNpcMountDetection;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class RegenerateStoredStaminaSystem extends EntityTickingSystem<EntityStore> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PlayerNpcMountDetection mountDetection = archetypeChunk.getComponent(i, PlayerNpcMountDetection.getComponentType());
        assert mountDetection != null;

        if (!mountDetection.shouldRegenerate()) {
            return;
        }

        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        EntityStatMap map = archetypeChunk.getComponent(i, EntityStatMap.getComponentType());
        assert map != null;

        int staminaIndex = DefaultEntityStatTypes.getStamina();
        EntityStatValue staminaValue = map.get(staminaIndex);
        if (staminaValue == null) {
            return;
        }
        // We could try a more complicated system where we replicate EntityStatsSystems.Regenerate, but that runs into issues when looking at the player's sprinting state etc. This is much simpler.
        // And of course, I could never be accused of overengineering a system
        mountDetection.addStoredStamina(PlayerNpcMountDetection.STAMINA_REGEN_RATE * v);
        LOGGER.atInfo().log("REGENERATING: " + mountDetection.getStoredStaminaValue());
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(PlayerNpcMountDetection.getComponentType(), EntityStatMap.getComponentType());
    }
}
