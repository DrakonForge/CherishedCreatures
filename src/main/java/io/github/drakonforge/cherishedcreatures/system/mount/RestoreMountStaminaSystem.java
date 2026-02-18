package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PlayerNpcMountDetection;
import io.github.drakonforge.cherishedcreatures.event.DismountNpcEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class RestoreMountStaminaSystem extends EntityEventSystem<EntityStore, DismountNpcEvent> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public RestoreMountStaminaSystem() {
        super(DismountNpcEvent.class);
    }

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl DismountNpcEvent dismountNpcEvent) {
        EntityStatMap playerStatMap = archetypeChunk.getComponent(i, EntityStatMap.getComponentType());
        PlayerNpcMountDetection mountDetection = archetypeChunk.getComponent(i, PlayerNpcMountDetection.getComponentType());
        assert playerStatMap != null;
        assert mountDetection != null;

        Ref<EntityStore> oldMount = dismountNpcEvent.getOldMountRef();
        int staminaIndex = DefaultEntityStatTypes.getStamina();
        EntityStatMap mountStatMap = store.getComponent(oldMount, EntityStatMap.getComponentType());
        if (mountStatMap == null) {
            return;
        }
        EntityStatValue playerStamina = playerStatMap.get(staminaIndex);
        if (playerStamina == null) {
            return;
        }
        // Save the stamina value inside the mount
        mountStatMap.setStatValue(staminaIndex, playerStamina.get());

        // Remove the modifier
        if (playerStatMap.getModifier(staminaIndex, PlayerNpcMountDetection.MOUNT_MAX_STAMINA_MODIFIER) != null) {
            playerStatMap.removeModifier(staminaIndex, PlayerNpcMountDetection.MOUNT_MAX_STAMINA_MODIFIER);
        }
        LOGGER.atInfo().log("Restoring player stamina: " + playerStamina.get() + " -> " + mountDetection.getStoredStaminaValue());
        playerStatMap.setStatValue(staminaIndex, mountDetection.getStoredStaminaValue());
        playerStatMap.update();
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), EntityStatMap.getComponentType(), PlayerNpcMountDetection.getComponentType());
    }
}
