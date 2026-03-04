package io.github.drakonforge.cherishedcreatures.ui;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.NumericAttribute;
import io.github.drakonforge.cherishedcreatures.asset.NumericAttribute.Mode;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.asset.PetType.PetFeatureFlag;
import io.github.drakonforge.cherishedcreatures.component.PetAttributes;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.ui.PetUIDetails.PetNumericAttributeDisplay.BarType;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

// Additional details when viewing the pet
public record PetUIDetails(List<PetNumericAttributeDisplay> numericAttributes) {
    public static PetUIDetails fromTrackedPetEntry(@NonNullDecl TrackedPetEntry entry, Holder<EntityStore> holder) {
        // TODO
        PetType petType = entry.getPetType();
        PetAttributes petAttributes = holder.getComponent(PetAttributes.getComponentType());

        List<PetNumericAttributeDisplay> numericAttributes = new ArrayList<>();

        // TODO: L10n support
        addNumericAttribute(numericAttributes, petType.getBaseHealthModifier(), PetAttributes.HEALTH, "Health", petAttributes);
        addNumericAttribute(numericAttributes, petType.getBaseStaminaModifier(), PetAttributes.STAMINA, "Stamina", petAttributes);
        if (petType.hasFeatureFlag(PetFeatureFlag.AdvancedMountHandling)) {
            addNumericAttribute(numericAttributes, petType.getMountBaseSpeed(), PetAttributes.MOUNT_BASE_SPEED, "Speed", petAttributes);
            addNumericAttribute(numericAttributes, petType.getMountGaitAcceleration(), PetAttributes.MOUNT_GAIT_ACCELERATION, "Acceleration", petAttributes);
        }
        return new PetUIDetails(numericAttributes);
    }

    // TODO: Tooltip, maybe color?
    public record PetNumericAttributeDisplay(String label, BarType barType, float percentage, float potentialPercentage) {
        public enum BarType {
            FIVE_SEGMENT
        }
    }

    private static void addNumericAttribute(List<PetNumericAttributeDisplay> numericAttributes, NumericAttribute numericAttribute, String key, String label, PetAttributes petAttributes) {
        if (numericAttribute.getMode() != Mode.Display) {
            return;
        }
        float value = petAttributes.getOrDefault(key, 0.0f);
        float max = numericAttribute.getMax();
        float min = numericAttribute.getMin();
        float percentage = Math.clamp((value - min) / (max - min), 0.0f, 1.0f);

        // TODO: Hardcoded bar type for now
        // TODO: Calculate potential percentage via numeric attribute & bonding level
        numericAttributes.add(new PetNumericAttributeDisplay(label, BarType.FIVE_SEGMENT, percentage, 0.75f));
    }
}
