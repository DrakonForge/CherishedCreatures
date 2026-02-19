package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.asset.PetType.PetFeatureFlag;
import io.github.drakonforge.cherishedcreatures.component.PetBondComponent;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.component.PetStateComponent;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class RegisterPetComponentsSystem extends RefChangeSystem<EntityStore, PetComponent> {

    @NonNullDecl
    @Override
    public ComponentType<EntityStore, PetComponent> componentType() {
        return PetComponent.getComponentType();
    }

    @Override
    public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PetComponent petComponent, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PetTypeComponent petTypeComponent = store.getComponent(ref, PetTypeComponent.getComponentType());
        DisplayNameComponent displayNameComponent = store.getComponent(ref, DisplayNameComponent.getComponentType());
        assert petTypeComponent != null;
        assert displayNameComponent != null;
        PetType petType = petTypeComponent.getPetType();

        // Add relevant components
        commandBuffer.ensureComponent(ref, PetStateComponent.getComponentType());
        if (petType.hasFeatureFlag(PetFeatureFlag.Bonding)) {
            commandBuffer.ensureComponent(ref, PetBondComponent.getComponentType());
        }

        Message displayName = displayNameComponent.getDisplayName();
        if (displayName != null) {
            petComponent.setPetName(displayNameComponent.getDisplayName().getAnsiMessage());
        }
    }

    @Override
    public void onComponentSet(@NonNullDecl Ref<EntityStore> ref,
            @NullableDecl PetComponent petComponent, @NonNullDecl PetComponent t1,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

    }

    @Override
    public void onComponentRemoved(@NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PetComponent petComponent, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PetTypeComponent petTypeComponent = store.getComponent(ref, PetTypeComponent.getComponentType());
        assert petTypeComponent != null;
        PetType petType = petTypeComponent.getPetType();

        commandBuffer.tryRemoveComponent(ref, PetStateComponent.getComponentType());
        if (petType.hasFeatureFlag(PetFeatureFlag.Bonding)) {
            store.tryRemoveComponent(ref, PetBondComponent.getComponentType());
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(PetTypeComponent.getComponentType(), DisplayNameComponent.getComponentType());
    }
}
