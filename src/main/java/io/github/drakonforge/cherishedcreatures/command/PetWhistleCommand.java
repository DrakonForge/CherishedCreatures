package io.github.drakonforge.cherishedcreatures.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.components.messaging.BeaconSupport;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class PetWhistleCommand extends AbstractPlayerCommand {

    private static final double WHISTLE_RANGE = 200.0;
    private static final float BEACON_EXPIRATION_TIME = 1.0f;

    // TODO: Add support for different kinds of whistles
    // TODO: Add support for whistle range

    public PetWhistleCommand() {
        super("whistle", "TODO");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext,
            @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        PlayerPetTracker petTracker = store.getComponent(ref, PlayerPetTracker.getComponentType());
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (petTracker == null || transformComponent == null) {
            commandContext.sendMessage(Message.raw("Missing components"));
            return;
        }

        for (TrackedPetEntry petEntry : petTracker.getPetEntries()) {
            Ref<EntityStore> petRef = petEntry.getEntityRef();
            if (petRef == null || !petRef.isValid()) {
                continue;
            }
            TransformComponent petTransform = store.getComponent(petRef, TransformComponent.getComponentType());
            BeaconSupport beaconSupport = store.getComponent(petRef, BeaconSupport.getComponentType());
            if (petTransform == null || beaconSupport == null) {
                continue;
            }
            double distSq = petTransform.getPosition().distanceSquaredTo(transformComponent.getPosition());
            if (distSq <= WHISTLE_RANGE * WHISTLE_RANGE) {
                beaconSupport.postMessage("Whistle_Recall", ref, BEACON_EXPIRATION_TIME);
            }
        }

    }
}
