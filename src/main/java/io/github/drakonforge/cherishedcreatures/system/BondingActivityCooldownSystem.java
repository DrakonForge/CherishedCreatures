package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PetBondComponent;
import io.github.drakonforge.cherishedcreatures.event.ReceivePetUpdatesEvent;
import io.github.drakonforge.cherishedcreatures.update.PetUpdate;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class BondingActivityCooldownSystem extends EntityTickingSystem<EntityStore> {

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return PetBondComponent.getComponentType();
    }

    @Override
    public void tick(float deltaTime,
                     int i,
                     @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                     @NonNullDecl Store<EntityStore> store,
                     @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PetBondComponent petBondComponent = archetypeChunk.getComponent(i,PetBondComponent.getComponentType());
        assert petBondComponent != null;

        Object2FloatMap<String> activityCooldowns = petBondComponent.getActivityCooldowns();
        for (Object2FloatMap.Entry<String> activityEntry : activityCooldowns.object2FloatEntrySet()) {
            activityEntry.setValue(Math.max(activityEntry.getFloatValue() - deltaTime,0.0f));
        }
    }
}
