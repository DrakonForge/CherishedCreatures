package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.PetType.PetFeatureFlag;
import io.github.drakonforge.cherishedcreatures.component.MountStatusMetersComponent;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
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

        Ref<EntityStore> mountRef = mountNpcEvent.getNewMountRef();
        PetTypeComponent petTypeComponent = store.getComponent(mountRef, PetTypeComponent.getComponentType());
        boolean showStamina = false;
        boolean showHealth = true;

        if (petTypeComponent != null) {
            showStamina = petTypeComponent.getPetType().hasFeatureFlag(PetFeatureFlag.AdvancedMountHandling);
            showHealth = !petTypeComponent.getPetType().hasFeatureFlag(PetFeatureFlag.Immortal);
        }

        UpdateMountStatusMetersSystem.updateStatusMeters(mountRef, store, statusMeters);

        if (showStamina) {
            statusMeters.getStaminaMeter().show();
        }
        if (showHealth) {
            statusMeters.getHealthMeter().show();
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return MountStatusMetersComponent.getComponentType();
    }
}
