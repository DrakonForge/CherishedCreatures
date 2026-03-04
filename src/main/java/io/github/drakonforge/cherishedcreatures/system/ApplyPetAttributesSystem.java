package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier.ModifierTarget;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier.CalculationType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PetAttributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApplyPetAttributesSystem extends RefSystem<EntityStore> {

    private static final String MODIFIER_KEY = "Pet_Attribute";

    @Override
    public void onEntityAdded(@NotNull Ref<EntityStore> ref, @NotNull AddReason addReason,
            @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer) {
        PetAttributes petAttributes = store.getComponent(ref, PetAttributes.getComponentType());
        EntityStatMap entityStatMap = store.getComponent(ref, EntityStatMap.getComponentType());
        assert petAttributes != null;
        assert entityStatMap != null;

        if (petAttributes.hasAttribute(PetAttributes.HEALTH)) {
            int healthIndex = DefaultEntityStatTypes.getHealth();
            float healthModifier = petAttributes.get(PetAttributes.HEALTH);
            entityStatMap.putModifier(healthIndex, MODIFIER_KEY,
                    new StaticModifier(ModifierTarget.MAX, CalculationType.ADDITIVE, healthModifier));
            if (addReason == AddReason.SPAWN) {
                entityStatMap.maximizeStatValue(healthIndex);
            }
        }

        if (petAttributes.hasAttribute(PetAttributes.STAMINA)) {
            float staminaModifier = petAttributes.get(PetAttributes.STAMINA);
            entityStatMap.putModifier(DefaultEntityStatTypes.getHealth(), MODIFIER_KEY,
                    new StaticModifier(ModifierTarget.MAX, CalculationType.ADDITIVE, staminaModifier));
        }
    }

    @Override
    public void onEntityRemove(@NotNull Ref<EntityStore> ref, @NotNull RemoveReason removeReason,
            @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer) {

    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(PetAttributes.getComponentType(), EntityStatMap.getComponentType());
    }
}
