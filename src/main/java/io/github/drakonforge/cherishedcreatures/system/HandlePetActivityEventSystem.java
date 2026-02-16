package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.PetActivity;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.component.PetBondComponent;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import io.github.drakonforge.cherishedcreatures.event.BondingXpEvent;
import io.github.drakonforge.cherishedcreatures.event.TriggerPetActivityEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class HandlePetActivityEventSystem extends EntityEventSystem<EntityStore, TriggerPetActivityEvent> {

    public HandlePetActivityEventSystem() {
        super(TriggerPetActivityEvent.class);
    }

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl TriggerPetActivityEvent petActivityEvent) {
        PetTypeComponent petTypeComponent = archetypeChunk.getComponent(i, PetTypeComponent.getComponentType());
        PetBondComponent petBondComponent = archetypeChunk.getComponent(i, PetBondComponent.getComponentType());
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        assert petTypeComponent != null;
        assert petBondComponent != null;
        PetType petType = petTypeComponent.getPetType();

        // TODO: This would be easier if the component mapped to BondingActivity directly
        String[] petActivities = petType.getPetActivities();
        float xpEarned = 0.0f;
        for (String activityName : petActivities) {
            PetActivity activity = PetActivity.getAssetStore().getAssetMap().getAsset(activityName);
            if (activity != null && activity.getPetActivityType() == petActivityEvent.getType()) {
                if (petBondComponent.isActivityOnCooldown(activityName) && !petActivityEvent.isForced()) {
                    continue;
                }
                petBondComponent.setActivityCooldown(activityName, activity.getCooldown());
                xpEarned += activity.getBaseXp();
                // TODO: Also track happiness gain
            }
        }
        if (xpEarned > 0.0f) {
            store.invoke(ref, new BondingXpEvent(xpEarned));
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(PetComponent.getComponentType(), PetTypeComponent.getComponentType(), PetBondComponent.getComponentType());
    }
}
