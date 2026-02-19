package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.MountStatusMetersComponent;
import io.github.drakonforge.cherishedcreatures.event.MountNpcEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class ShowMountStatusMetersSystem extends MountNpcEventSystem {

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl MountNpcEvent mountNpcEvent) {
        MountStatusMetersComponent statusMeters = archetypeChunk.getComponent(i, MountStatusMetersComponent.getComponentType());
        assert statusMeters != null;
        statusMeters.getHealthMeter().show();
        statusMeters.getStaminaMeter().show();
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return MountStatusMetersComponent.getComponentType();
    }
}
