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
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier.ModifierTarget;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier.CalculationType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PlayerNpcMountDetection;
import io.github.drakonforge.cherishedcreatures.event.MountNpcEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class UseMountStaminaSystem extends EntityEventSystem<EntityStore, MountNpcEvent> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public UseMountStaminaSystem() {
        super(MountNpcEvent.class);
    }

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl MountNpcEvent mountNpcEvent) {
        EntityStatMap playerStatMap = archetypeChunk.getComponent(i, EntityStatMap.getComponentType());
        PlayerNpcMountDetection mountDetection = archetypeChunk.getComponent(i, PlayerNpcMountDetection.getComponentType());
        assert playerStatMap != null;
        assert mountDetection != null;

        Ref<EntityStore> newMount = mountNpcEvent.getNewMountRef();
        int staminaIndex = DefaultEntityStatTypes.getStamina();

        EntityStatMap mountStatMap = store.getComponent(newMount, EntityStatMap.getComponentType());
        if (mountStatMap == null) {
            return;
        }
        EntityStatValue mountStamina = mountStatMap.get(staminaIndex);
        if (mountStamina == null) {
            return;
        }
        EntityStatValue playerStamina = playerStatMap.get(staminaIndex);
        if (playerStamina == null) {
            return;
        }
        LOGGER.atInfo().log("Using mount stamina: " + playerStamina.get() + " -> " + mountStamina.get());
        mountDetection.setLastPlayerStaminaValue(playerStamina.get(), playerStamina.getMax());
        playerStatMap.setStatValue(staminaIndex, mountStamina.get());
        // ConstantModifier has some codec issues, so for now we just calculate the difference between the mount and player stamina
        // This is flimsy and will fall apart if anything else changes max stamina
        playerStatMap.putModifier(staminaIndex, PlayerNpcMountDetection.MOUNT_MAX_STAMINA_MODIFIER, new StaticModifier(
            ModifierTarget.MAX, CalculationType.ADDITIVE, mountStamina.getMax() - playerStamina.getMax()));
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), EntityStatMap.getComponentType(), PlayerNpcMountDetection.getComponentType());
    }
}
