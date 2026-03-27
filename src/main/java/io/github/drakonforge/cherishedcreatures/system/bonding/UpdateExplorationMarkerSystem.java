package io.github.drakonforge.cherishedcreatures.system.bonding;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PetBondComponent;
import io.github.drakonforge.cherishedcreatures.data.PetActivityType;
import io.github.drakonforge.cherishedcreatures.event.TriggerPetActivityEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class UpdateExplorationMarkerSystem extends DelayedEntitySystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public UpdateExplorationMarkerSystem() {super(1.0f);}

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PetBondComponent petBondComponent = archetypeChunk.getComponent(i,
                PetBondComponent.getComponentType());
        assert petBondComponent != null;

        TransformComponent petTransformComponent = archetypeChunk.getComponent(i,
                TransformComponent.getComponentType());
        assert petTransformComponent != null;

        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);

        Vector3d petPosition = petTransformComponent.getPosition();
        Vector3d lastMarker = petBondComponent.getLastExplorationMarker();
        double distance = petPosition.distanceTo(lastMarker);

        // Should be refactored to initialize component with pet's position when given bond component
        if (lastMarker.closeToZero(0.1)) {
            // Initialize lastMarker to current entityPosition
            petBondComponent.setLastExplorationMarker(petPosition.x, petPosition.y, petPosition.z);
            LOGGER.atInfo()
                    .log("Pet initial exploration position set to %f, %f, %f", petPosition.x,
                            petPosition.y, petPosition.z);

            return;
        }
        if (distance >= 10.0f) {
            // Trigger exploration activity event
            commandBuffer.invoke(ref, new TriggerPetActivityEvent(PetActivityType.Exploring));
            petBondComponent.setLastExplorationMarker(petPosition.x, petPosition.y, petPosition.z);

            LOGGER.atInfo()
                    .log("Fired exploration activity for pet at %f, %f, %f", petPosition.x,
                            petPosition.y, petPosition.z);
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {

        return Query.and(PetBondComponent.getComponentType(),
                TransformComponent.getComponentType());
    }
}
