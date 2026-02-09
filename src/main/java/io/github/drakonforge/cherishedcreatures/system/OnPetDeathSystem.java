package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
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
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl DeathComponent deathComponent, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PetComponent petComponent = store.getComponent(ref, PetComponent.getComponentType());
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
        assert petComponent != null;
        assert uuidComponent != null;

        UUID ownerUuid = petComponent.getOwnerUuid();
        if (ownerUuid == null) {
            LOGGER.atSevere().log("On entity death, owner UUID is null for " + uuidComponent.getUuid());
            return;
        }
        PlayerPetTracker ownerTracker = OfflinePlayerHelpers.getComponent(store, ownerUuid, PlayerPetTracker.getComponentType());
        if (ownerTracker == null) {
            return;
        }
        TrackedPetEntry entry = ownerTracker.getPetEntry(uuidComponent.getUuid());
        if (entry == null) {
            return;
        }
        entry.setStatus(Status.DEAD); // TODO: Depending on type of pet, it might be resurrected or just stored again
        OfflinePlayerHelpers.saveIfOffline(ownerUuid);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(UUIDComponent.getComponentType(), PetComponent.getComponentType(), PetTypeComponent.getComponentType());
    }
}
