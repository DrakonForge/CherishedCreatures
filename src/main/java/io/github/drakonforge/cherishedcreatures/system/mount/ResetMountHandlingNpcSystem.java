package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingNpcComponent;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResetMountHandlingNpcSystem extends RefChangeSystem<EntityStore, MountHandlingNpcComponent> {

    @NotNull
    @Override
    public ComponentType<EntityStore, MountHandlingNpcComponent> componentType() {
        return MountHandlingNpcComponent.getComponentType();
    }

    @Override
    public void onComponentAdded(@NotNull Ref<EntityStore> ref,
            @NotNull MountHandlingNpcComponent mountHandlingNpcComponent,
            @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer) {

    }

    @Override
    public void onComponentSet(@NotNull Ref<EntityStore> ref,
            @Nullable MountHandlingNpcComponent mountHandlingNpcComponent,
            @NotNull MountHandlingNpcComponent t1, @NotNull Store<EntityStore> store,
            @NotNull CommandBuffer<EntityStore> commandBuffer) {

    }

    @Override
    public void onComponentRemoved(@NotNull Ref<EntityStore> ref,
            @NotNull MountHandlingNpcComponent mountHandlingNpcComponent,
            @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer) {
        NPCEntity npcEntity = store.getComponent(ref,
                Objects.requireNonNull(NPCEntity.getComponentType()));
        assert npcEntity != null;

        // Reset movement animation
        npcEntity.playAnimation(ref, AnimationSlot.Movement, null, store);
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(MountHandlingNpcComponent.getComponentType(), PetTypeComponent.getComponentType(),
                NPCEntity.getComponentType());
    }
}
