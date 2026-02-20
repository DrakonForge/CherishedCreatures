package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import io.github.drakonforge.cherishedcreatures.component.PlayerNpcMountDetection;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

// Update state from the mounted entity
// TODO: Set dependencies
public class MountHandlingUpdateStateSystem extends EntityTickingSystem<EntityStore> {

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PlayerNpcMountDetection mountDetection = archetypeChunk.getComponent(i, PlayerNpcMountDetection.getComponentType());
        MountHandlingComponent mountHandlingComponent = archetypeChunk.getComponent(i, MountHandlingComponent.getComponentType());
        assert mountDetection != null;
        assert mountHandlingComponent != null;

        Ref<EntityStore> mountRef = mountDetection.getCurrentMount();
        if (mountRef == null || !mountRef.isValid()) {
            return;
        }


        EntityStatMap mountStats = store.getComponent(mountRef, EntityStatMap.getComponentType());
        if (mountStats != null) {
            EntityStatValue staminaValue = mountStats.get(DefaultEntityStatTypes.getStamina());
            if (staminaValue != null) {
                mountHandlingComponent.setStaminaDepleted(staminaValue.get() <= 0.0f);
            }
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(MountHandlingComponent.getComponentType(), PlayerNpcMountDetection.getComponentType());
    }
}
