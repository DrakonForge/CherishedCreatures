package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PlayerNpcMountDetection;
import io.github.drakonforge.cherishedcreatures.event.DismountNpcEvent;
import io.github.drakonforge.cherishedcreatures.event.MountNpcEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class DetectNpcMountSystem extends
        EntityTickingSystem<EntityStore> {

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), PlayerNpcMountDetection.getComponentType());
    }

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = archetypeChunk.getComponent(i, Player.getComponentType());
        PlayerNpcMountDetection mountDetection = archetypeChunk.getComponent(i, PlayerNpcMountDetection.getComponentType());
        assert player != null;
        assert mountDetection != null;
        Ref<EntityStore> playerRef = archetypeChunk.getReferenceTo(i);
        int mountNetworkId = player.getMountEntityId();
        Ref<EntityStore> previousMount = mountDetection.getCurrentMount();
        if (mountNetworkId == 0) {
            if (previousMount != null && previousMount.isValid()) {
                // Mount no longer exists
                commandBuffer.invoke(playerRef, new DismountNpcEvent(previousMount));
                mountDetection.setCurrentMount(null);
            }
        } else {
            Ref<EntityStore> newMount = store.getExternalData().getRefFromNetworkId(mountNetworkId);
            if (previousMount != null && previousMount.isValid()) {
                NetworkId networkId = store.getComponent(previousMount, NetworkId.getComponentType());
                if (networkId != null && networkId.getId() == mountNetworkId) {
                    return;
                }
                // Mount has changed
                commandBuffer.invoke(playerRef, new DismountNpcEvent(previousMount));
            }
            // Mount now exists
            commandBuffer.invoke(playerRef, new MountNpcEvent(newMount));
            mountDetection.setCurrentMount(newMount);
        }
    }
}
