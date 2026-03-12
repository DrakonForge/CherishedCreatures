package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import io.github.drakonforge.cherishedcreatures.asset.NumericAttribute;
import io.github.drakonforge.cherishedcreatures.asset.NumericAttribute.Mode;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.asset.PetType.PetFeatureFlag;
import io.github.drakonforge.cherishedcreatures.component.PetAttributes;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import java.util.Random;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnsurePetAttributesSystem extends HolderSystem<EntityStore> {

    private static final Random random = new Random();

    @Override
    public void onEntityAdd(@NotNull Holder<EntityStore> holder, @NotNull AddReason addReason,
            @NotNull Store<EntityStore> store) {
        if (holder.getComponent(PetAttributes.getComponentType()) != null) {
            return;
        }
        PetTypeComponent petTypeComponent = holder.getComponent(PetTypeComponent.getComponentType());
        assert petTypeComponent != null;

        PetAttributes defaultPetAttributes = new PetAttributes();
        PetType petType = petTypeComponent.getPetType();

        initializeAttribute(defaultPetAttributes, PetAttributes.HEALTH, petType.getBaseHealthModifier());
        initializeAttribute(defaultPetAttributes, PetAttributes.STAMINA, petType.getBaseStaminaModifier());

        if (petType.hasFeatureFlag(PetFeatureFlag.AdvancedMountHandling)) {
            initializeAttribute(defaultPetAttributes, PetAttributes.MOUNT_BASE_SPEED, petType.getMountBaseSpeed());
            initializeAttribute(defaultPetAttributes, PetAttributes.MOUNT_GAIT_ACCELERATION, petType.getMountGaitAcceleration());
        }

        holder.putComponent(PetAttributes.getComponentType(), defaultPetAttributes);
    }

    private void initializeAttribute(PetAttributes petAttributes, String key, NumericAttribute numericAttribute) {
        if (numericAttribute.getMode() == Mode.Disabled) {
            return;
        }

        float average = numericAttribute.getAverage();
        float standardDeviation = numericAttribute.getStandardDeviation();
        float value = (float) random.nextGaussian(average, standardDeviation);
        float clampedValue = numericAttribute.clamp(value);
        petAttributes.putBaseAttribute(key, clampedValue);
    }

    @Override
    public void onEntityRemoved(@NotNull Holder<EntityStore> holder,
            @NotNull RemoveReason removeReason, @NotNull Store<EntityStore> store) {

    }

    @NotNull
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(new SystemDependency<>(Order.AFTER, RegisterDefaultPetTypeSystem.class));
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(NPCEntity.getComponentType(), PetTypeComponent.getComponentType());
    }
}
