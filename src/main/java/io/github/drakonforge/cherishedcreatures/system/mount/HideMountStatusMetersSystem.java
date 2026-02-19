package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.MountStatusMetersComponent;
import io.github.drakonforge.cherishedcreatures.event.DismountNpcEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class HideMountStatusMetersSystem extends DismountNpcEventSystem {

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl DismountNpcEvent dismountNpcEvent) {
        MountStatusMetersComponent statusMeters = archetypeChunk.getComponent(i,
                MountStatusMetersComponent.getComponentType());
        assert statusMeters != null;
        statusMeters.getHealthMeter().hide();
        statusMeters.getStaminaMeter().hide();
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return MountStatusMetersComponent.getComponentType();
    }
}
