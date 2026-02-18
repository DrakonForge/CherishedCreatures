package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PlayerNpcMountDetection;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class RegisterNpcMountDetectionSystem extends HolderSystem<EntityStore> {

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), EntityStatMap.getComponentType());
    }

    @Override
    public void onEntityAdd(@NonNullDecl Holder<EntityStore> holder,
            @NonNullDecl AddReason addReason, @NonNullDecl Store<EntityStore> store) {
        EntityStatMap entityStatMap = holder.getComponent(EntityStatMap.getComponentType());
        assert entityStatMap != null;

        int staminaIndex = DefaultEntityStatTypes.getStamina();
        if (entityStatMap.getModifier(staminaIndex, PlayerNpcMountDetection.MOUNT_MAX_STAMINA_MODIFIER) != null) {
            entityStatMap.removeModifier(staminaIndex,
                    PlayerNpcMountDetection.MOUNT_MAX_STAMINA_MODIFIER);
        }
        holder.ensureComponent(PlayerNpcMountDetection.getComponentType());

    }

    @Override
    public void onEntityRemoved(@NonNullDecl Holder<EntityStore> holder,
            @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store) {

    }
}
