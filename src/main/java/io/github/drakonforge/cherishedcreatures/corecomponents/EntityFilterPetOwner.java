package io.github.drakonforge.cherishedcreatures.corecomponents;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.EntityFilterBase;
import com.hypixel.hytale.server.npc.role.Role;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.corecomponents.builder.BuilderEntityFilterPetOwner;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class EntityFilterPetOwner extends EntityFilterBase {
    public static final int COST = 0;

    public EntityFilterPetOwner(@Nonnull BuilderEntityFilterPetOwner builder) {

    }

    @Override
    public boolean matchesEntity(@NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl Ref<EntityStore> targetRef, @NonNullDecl Role role,
            @NonNullDecl Store<EntityStore> store) {
        PetComponent petComponent = store.getComponent(ref, PetComponent.getComponentType());
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());

        if (petComponent == null || uuidComponent == null) {
            return false;
        }

        Player player = store.getComponent(targetRef, Player.getComponentType());
        PlayerPetTracker petTracker = store.getComponent(targetRef, PlayerPetTracker.getComponentType());
        if (player == null || petTracker == null) {
            return false;
        }

        UUID petUuid = uuidComponent.getUuid();
        return petTracker.getPetEntry(petUuid) != null;
    }

    @Override
    public int cost() {
        return COST;
    }
}
