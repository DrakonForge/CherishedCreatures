package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.builtin.mounts.MountedByComponent;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.asset.PetType.PetFeatureFlag;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class RegisterMountHandlingSystem extends RefChangeSystem<EntityStore, MountedByComponent> {

    @NonNullDecl
    @Override
    public ComponentType<EntityStore, MountedByComponent> componentType() {
        return MountedByComponent.getComponentType();
    }

    @Override
    public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl MountedByComponent mountedByComponent,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PetTypeComponent petTypeComponent = store.getComponent(ref, PetTypeComponent.getComponentType());
        assert petTypeComponent != null;
        PetType petType = petTypeComponent.getPetType();

        if (petType.hasFeatureFlag(PetFeatureFlag.AdvancedMountHandling)) {
            commandBuffer.addComponent(ref, MountHandlingComponent.getComponentType());
        }
    }

    @Override
    public void onComponentSet(@NonNullDecl Ref<EntityStore> ref,
            @NullableDecl MountedByComponent mountedByComponent, @NonNullDecl MountedByComponent t1,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

    }

    @Override
    public void onComponentRemoved(@NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl MountedByComponent mountedByComponent,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PetTypeComponent petTypeComponent = store.getComponent(ref, PetTypeComponent.getComponentType());
        assert petTypeComponent != null;
        PetType petType = petTypeComponent.getPetType();

        if (petType.hasFeatureFlag(PetFeatureFlag.AdvancedMountHandling)) {
            commandBuffer.tryRemoveComponent(ref, MountHandlingComponent.getComponentType());
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(PetTypeComponent.getComponentType());
    }
}
