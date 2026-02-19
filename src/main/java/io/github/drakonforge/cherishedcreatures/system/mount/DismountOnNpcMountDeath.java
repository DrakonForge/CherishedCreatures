package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.builtin.mounts.NPCMountComponent;
import com.hypixel.hytale.builtin.mounts.NPCMountSystems;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Set;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class DismountOnNpcMountDeath extends DeathSystems.OnDeathSystem {

    @Nonnull
    public Query<EntityStore> getQuery() {
        return NPCMountComponent.getComponentType();
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(
                new SystemDependency<>(Order.BEFORE, NPCMountSystems.DismountOnMountDeath.class));
    }

    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component,
            @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        NPCMountComponent mountComponent = store.getComponent(ref, NPCMountComponent.getComponentType());
        assert mountComponent != null;
        PlayerRef playerRef = mountComponent.getOwnerPlayerRef();
        if (playerRef != null) {
            Ref<EntityStore> playerEntityRef = playerRef.getReference();
            if (playerEntityRef != null && playerEntityRef.isValid()) {
                Player player = store.getComponent(playerEntityRef, Player.getComponentType());
                if (player != null) {
                    player.setMountEntityId(0);
                }
            }
        }

    }
}
