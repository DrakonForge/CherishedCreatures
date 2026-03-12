package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.NumericAttribute;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.asset.PetType.PetFeatureFlag;
import io.github.drakonforge.cherishedcreatures.component.PetAttributes;
import io.github.drakonforge.cherishedcreatures.component.PetBondComponent;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import io.github.drakonforge.cherishedcreatures.event.BondingLevelChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpdateAttributesOnLevelUpSystem extends BondingLevelChangeEventSystem {

    @Override
    public void handle(int i, @NotNull ArchetypeChunk<EntityStore> archetypeChunk,
            @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer,
            @NotNull BondingLevelChangeEvent bondingLevelChangeEvent) {
        PetTypeComponent petTypeComponent = archetypeChunk.getComponent(i, PetTypeComponent.getComponentType());
        PetBondComponent petBondComponent = archetypeChunk.getComponent(i, PetBondComponent.getComponentType());
        PetAttributes petAttributes = archetypeChunk.getComponent(i, PetAttributes.getComponentType());

        assert petTypeComponent != null;
        assert petBondComponent != null;
        assert petAttributes != null;

        int currentLevel = bondingLevelChangeEvent.getLevelChangedTo();
        PetType petType = petTypeComponent.getPetType();


        applyModifier(petAttributes, PetAttributes.HEALTH, petType.getBaseHealthModifier(), currentLevel);
        applyModifier(petAttributes, PetAttributes.STAMINA, petType.getBaseStaminaModifier(), currentLevel);
        if (petType.hasFeatureFlag(PetFeatureFlag.AdvancedMountHandling)) {
            applyModifier(petAttributes, PetAttributes.MOUNT_BASE_SPEED, petType.getMountBaseSpeed(), currentLevel);
            applyModifier(petAttributes, PetAttributes.MOUNT_GAIT_ACCELERATION, petType.getMountGaitAcceleration(), currentLevel);
        }
    }

    private void applyModifier(PetAttributes petAttributes, String attributeName, NumericAttribute attribute, int bondingLevel) {
        if (!petAttributes.hasAttribute(attributeName)) {
            return;
        }
        float increasePerLevel = attribute.getIncreasePerLevel();
        if (increasePerLevel == 0.0f) {
            return;
        }
        petAttributes.putAttributeModifier(attributeName, increasePerLevel * bondingLevel);
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(PetTypeComponent.getComponentType(), PetBondComponent.getComponentType(),
                PetAttributes.getComponentType());
    }
}
