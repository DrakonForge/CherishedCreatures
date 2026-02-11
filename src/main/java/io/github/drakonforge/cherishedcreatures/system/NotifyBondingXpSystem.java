package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.event.BondingXpEvent;
import java.util.UUID;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class NotifyBondingXpSystem extends BondingXpEventSystem {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl BondingXpEvent bondingXpEvent) {
        PetComponent petComponent = archetypeChunk.getComponent(i, PetComponent.getComponentType());
        DisplayNameComponent nameComponent = archetypeChunk.getComponent(i, DisplayNameComponent.getComponentType());
        assert petComponent != null;
        assert nameComponent != null;
        UUID ownerUuid = petComponent.getOwnerUuid();
        if (ownerUuid == null) {
            LOGGER.atWarning().log("Owner UUID is null, failed to notify");
            return;
        }
        PlayerRef player = Universe.get().getPlayer(ownerUuid);
        if (player == null) {
            return;
        }

        // TODO: Can add a throttle here where it only sends messages after reaching certain checkpoints, every 5 XP, etc.
        Message displayName = nameComponent.getDisplayName();
        String petName = displayName == null ? "Unnamed Pet" : displayName.getAnsiMessage();
        player.sendMessage(Message.raw("Your pet " + petName + " earned " + bondingXpEvent.getAmountGained() + " bonding XP!"));

    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(PetComponent.getComponentType(), DisplayNameComponent.getComponentType());
    }

    @NullableDecl
    @Override
    public SystemGroup<EntityStore> getGroup() {
        return CherishedCreaturesPlugin.get().getInspectBondingXpEventGroup();
    }
}
