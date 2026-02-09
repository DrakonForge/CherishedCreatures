package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry.Status;
import io.github.drakonforge.cherishedcreatures.util.OfflinePlayerHelpers;
import java.util.UUID;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class OnPetDeathSystem extends DeathSystems.OnDeathSystem {

    @Override
    public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl DeathComponent deathComponent, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PetComponent petComponent = store.getComponent(ref, PetComponent.getComponentType());
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
        assert petComponent != null;
        assert uuidComponent != null;

        UUID ownerUuid = petComponent.getOwnerUuid();
        PlayerPetTracker ownerTracker = OfflinePlayerHelpers.getComponent(store, ownerUuid, PlayerPetTracker.getComponentType());
        if (ownerTracker == null) {
            return;
        }
        TrackedPetEntry entry = ownerTracker.getPetEntry(uuidComponent.getUuid());
        if (entry == null) {
            return;
        }
        entry.setEntityRef(ref);
        entry.saveEntity(store);
        entry.setStatus(Status.DEAD);
        OfflinePlayerHelpers.saveIfOffline(ownerUuid);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(UUIDComponent.getComponentType(), PetComponent.getComponentType(), PetTypeComponent.getComponentType());
    }
}
