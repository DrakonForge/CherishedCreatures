package io.github.drakonforge.cherishedcreatures.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.components.messaging.BeaconSupport;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.util.TargetLookHelpers;
import it.unimi.dsi.fastutil.objects.ObjectList;
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

        Ref<EntityStore> nearest = TargetLookHelpers.getEntityNearestToCrosshair(ref, 64.0, Math.toRadians(15.0), store);
        if (nearest != null) {
            DisplayNameComponent nearestDisplayName = store.getComponent(nearest, DisplayNameComponent.getComponentType());
            TransformComponent nearestTransform = store.getComponent(nearest, TransformComponent.getComponentType());

            if (nearestDisplayName == null || nearestTransform == null) {
                LOGGER.atWarning().log("Missing components for whistle target");
                return;
            }
            String mobName = nearestDisplayName.getDisplayName() != null ? nearestDisplayName.getDisplayName().getAnsiMessage() : null;
            LOGGER.atInfo().log("Whistled at " + mobName);
            ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
            SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = store.getResource(
                    EntityModule.get().getPlayerSpatialResourceType());
            playerSpatialResource.getSpatialStructure().collect(nearestTransform.getPosition(),
                    75.0F, results);
            ParticleUtil.spawnParticleEffect("Angry", nearestTransform.getPosition().clone().add(new Vector3d(0, 2, 0)), nearestTransform.getRotation(), results, store);
        } else {
            LOGGER.atInfo().log("No whistle target");
        }


    }
}
