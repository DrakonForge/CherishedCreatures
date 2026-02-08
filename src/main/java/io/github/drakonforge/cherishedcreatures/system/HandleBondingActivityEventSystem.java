package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.BondingActivity;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.component.PetBondComponent;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import io.github.drakonforge.cherishedcreatures.event.BondingActivityEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class HandleBondingActivityEventSystem extends EntityEventSystem<EntityStore, BondingActivityEvent> {

    public HandleBondingActivityEventSystem() {
        super(BondingActivityEvent.class);
    }

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl BondingActivityEvent bondingActivityEvent) {
        PetTypeComponent petTypeComponent = archetypeChunk.getComponent(i, PetTypeComponent.getComponentType());
        PetBondComponent petBondComponent = archetypeChunk.getComponent(i, PetBondComponent.getComponentType());
        assert petTypeComponent != null;
        assert petBondComponent != null;
        PetType petType = petTypeComponent.getPetType();

        // TODO: This would be easier if the component mapped to BondingActivity directly
        String[] bondingActivities = petType.getBondingActivities();
        for (String activityName : bondingActivities) {
            BondingActivity activity = BondingActivity.getAssetStore().getAssetMap().getAsset(activityName);
            if (activity != null && activity.getBondingActivityType() == bondingActivityEvent.getType()) {
                petBondComponent.triggerActivity(activityName);
            }
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(PetComponent.getComponentType(), PetTypeComponent.getComponentType(), PetBondComponent.getComponentType());
    }
}
