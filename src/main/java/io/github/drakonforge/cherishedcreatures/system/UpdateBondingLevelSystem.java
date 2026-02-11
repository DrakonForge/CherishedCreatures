package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.component.PetBondComponent;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import io.github.drakonforge.cherishedcreatures.event.BondingXpEvent;
import io.github.drakonforge.cherishedcreatures.util.BondingHelpers;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class UpdateBondingLevelSystem extends BondingXpEventSystem {

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl BondingXpEvent bondingXpEvent) {
        PetTypeComponent petTypeComponent = archetypeChunk.getComponent(i, PetTypeComponent.getComponentType());
        PetBondComponent petBondComponent = archetypeChunk.getComponent(i, PetBondComponent.getComponentType());
        assert petTypeComponent != null;
        assert petBondComponent != null;
        PetType petType = petTypeComponent.getPetType();
        float[] bondingLevelValues = petType.getBondingLevelValues();
        float bondingXp = petBondComponent.getBondingXp();
        int newBondingLevel = BondingHelpers.getBondingLevel(bondingLevelValues, bondingXp);
        // TODO: Notifications upon gaining a level?
        petBondComponent.setBondingLevel(newBondingLevel);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(PetTypeComponent.getComponentType(), PetBondComponent.getComponentType());
    }

    @NullableDecl
    @Override
    public SystemGroup<EntityStore> getGroup() {
        return CherishedCreaturesPlugin.get().getInspectBondingXpEventGroup();
    }
}
