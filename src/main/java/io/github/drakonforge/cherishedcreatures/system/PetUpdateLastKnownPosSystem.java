package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PetUpdateLastKnownPosSystem extends DelayedEntitySystem<EntityStore> {

    public PetUpdateLastKnownPosSystem() {
        super(1.0f);
    }

    @Override
    public void tick(float v, int i, @NotNull ArchetypeChunk<EntityStore> archetypeChunk,
            @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer) {
        PetComponent petComponent = archetypeChunk.getComponent(i, PetComponent.getComponentType());
        UUIDComponent uuidComponent = archetypeChunk.getComponent(i, UUIDComponent.getComponentType());
        TransformComponent transformComponent = archetypeChunk.getComponent(i, TransformComponent.getComponentType());
        assert petComponent != null;
        assert uuidComponent != null;
        assert transformComponent != null;

        UUID ownerUuid = petComponent.getOwnerUuid();
        if (ownerUuid == null) {
            return;
        }
        PlayerRef playerRef = Universe.get().getPlayer(ownerUuid);
        if (playerRef == null) {
            return;
        }
        Ref<EntityStore> ownerRef = playerRef.getReference();
        if (ownerRef == null || !ownerRef.isValid()) {
            return;
        }
        PlayerPetTracker petTracker = store.getComponent(ownerRef, PlayerPetTracker.getComponentType());
        if (petTracker == null) {
            return;
        }
        TrackedPetEntry petEntry = petTracker.getPetEntry(uuidComponent.getUuid());
        if (petEntry == null) {
            return;
        }
        petEntry.setLastKnownPos(transformComponent.getPosition().clone());
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return PetComponent.getComponentType();
    }
}
