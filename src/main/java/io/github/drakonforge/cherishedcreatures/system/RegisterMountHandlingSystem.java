package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.builtin.mounts.MountedComponent;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.PetType.PetFeatureFlag;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class RegisterMountHandlingSystem extends RefChangeSystem<EntityStore, MountedComponent> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }

    @NonNullDecl
    @Override
    public ComponentType<EntityStore, MountedComponent> componentType() {
        return MountedComponent.getComponentType();
    }

    @Override
    public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl MountedComponent mountedComponent, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        LOGGER.atInfo().log("ADD");
        Ref<EntityStore> mountRef = mountedComponent.getMountedToEntity();
        if (mountRef == null) {
            return;
        }
        PetTypeComponent petTypeComponent = store.getComponent(mountRef, PetTypeComponent.getComponentType());
        if (petTypeComponent != null && petTypeComponent.getPetType().hasFeatureFlag(PetFeatureFlag.AdvancedMountHandling)) {
            commandBuffer.addComponent(mountRef, MountHandlingComponent.getComponentType());
        }
    }

    @Override
    public void onComponentSet(@NonNullDecl Ref<EntityStore> ref,
            @NullableDecl MountedComponent mountedComponent, @NonNullDecl MountedComponent t1,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

    }

    @Override
    public void onComponentRemoved(@NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl MountedComponent mountedComponent, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        LOGGER.atInfo().log("REMOVE");
        Ref<EntityStore> mountRef = mountedComponent.getMountedToEntity();
        if (mountRef == null) {
            return;
        }
        PetTypeComponent petTypeComponent = store.getComponent(mountRef, PetTypeComponent.getComponentType());
        if (petTypeComponent != null && petTypeComponent.getPetType().hasFeatureFlag(PetFeatureFlag.AdvancedMountHandling)) {
            commandBuffer.tryRemoveComponent(mountRef, MountHandlingComponent.getComponentType());
        }
    }
}
