package io.github.drakonforge.cherishedcreatures.ui;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.asset.PetType.PetFeatureFlag;
import io.github.drakonforge.cherishedcreatures.component.PetBondComponent;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry.Status;
import io.github.drakonforge.cherishedcreatures.ui.PetUICard.PetUIBondingInfo.Type;
import io.github.drakonforge.cherishedcreatures.util.BondingHelpers;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public record PetUICard(UUID id, String name, String roleName, String iconPath, Status status, boolean isLoaded, boolean showSummonToggle, @Nullable PetUIHealthInfo healthInfo, @Nullable PetUIBondingInfo bondingInfo, @Nullable PetUIDetails details, @Nullable Integer index) {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public record PetUIBondingInfo(Type type, float fillProgress, int bondingLevel) {
        public enum Type {
            FOUR_SEGMENT, LINEAR
        }
    }

    public record PetUIHealthInfo(float fillProgress, int value, int max) {}

    public static PetUICard fromTrackedPetEntry(@NonNullDecl TrackedPetEntry entry, @NonNullDecl Store<EntityStore> store, boolean generateDetails, int index) {
        entry.attemptSaveEntityFromLive(store);
        Holder<EntityStore> holder = entry.getHolder(false);
        PetType petType = entry.getPetType();
        String roleName = "Unknown";
        String iconPath = petType.getIconPath();

        NPCEntity npcEntity =
                holder.getComponent(Objects.requireNonNull(NPCEntity.getComponentType()));
        if (npcEntity != null) {
            roleName = npcEntity.getRoleName();
            // if (role != null) {
            //     roleName = Message.translation(npcEntity.getRole().getNameTranslationKey()).getAnsiMessage();
            // }
        }

        String displayName = getDisplayName(holder);
        Status status = entry.getStatus();
        PetUIBondingInfo bondingInfo = getBondingInfo(petType, holder);
        PetUIHealthInfo healthInfo = getHealthInfo(petType, status, holder);
        boolean showSummonToggle = petType.hasFeatureFlag(PetFeatureFlag.SummonControls) && canSummonPetWithStatus(entry.getStatus());
        PetUIDetails details = null;
        if (generateDetails) {
            details = PetUIDetails.fromTrackedPetEntry(entry, holder);
        }
        // If the pet has Status ALIVE but is unloaded, we basically treat it as un-summoned for our purposes.
        return new PetUICard(entry.getUuid(), displayName, roleName, iconPath, status, entry.isLoaded(), showSummonToggle, healthInfo, bondingInfo, details, index);
    }

    private static boolean canSummonPetWithStatus(Status status) {
        return status == Status.STORED || status == Status.ALIVE;
    }

    @Nonnull
    private static String getDisplayName(Holder<EntityStore> holder) {
        PetComponent petComponent = holder.getComponent(PetComponent.getComponentType());
        DisplayNameComponent displayNameComponent = holder.getComponent(DisplayNameComponent.getComponentType());
        if (petComponent != null) {
            String petName = petComponent.getPetName();
            if (petName != null) {
                return petName;
            }
        }

        // Fallback to display name component
        if (displayNameComponent != null) {
            Message displayName = displayNameComponent.getDisplayName();
            if (displayName != null) {
                return displayName.getAnsiMessage();
            }
        }

        // Last fallback
        return "Your Pet";
    }

    @Nullable
    private static PetUIBondingInfo getBondingInfo(PetType petType, Holder<EntityStore> holder) {
        if (!petType.hasFeatureFlag(PetFeatureFlag.Bonding)) {
            return null;
        }
        PetBondComponent petBondComponent = holder.getComponent(PetBondComponent.getComponentType());
        if (petBondComponent == null) {
            return null;
        }
        int bondingLevel = petBondComponent.getBondingLevel();
        float bondingXp = petBondComponent.getBondingXp();
        float[] bondingLevelValues = petType.getBondingLevelValues();
        Type type;
        float totalProgress;

        if (bondingLevelValues.length == BondingHelpers.DEFAULT_NUM_SEGMENTS) {
            totalProgress = BondingHelpers.getSegmentedBondingProgress(bondingLevelValues, bondingXp);
            type = Type.FOUR_SEGMENT;
        } else {
            totalProgress = BondingHelpers.getLinearBondingProgress(bondingLevelValues, bondingXp);
            type = Type.LINEAR;
        }
        return new PetUIBondingInfo(type, totalProgress, bondingLevel);
    }

    @Nullable
    private static PetUIHealthInfo getHealthInfo(PetType petType, Status status, Holder<EntityStore> holder) {
        if (petType.hasFeatureFlag(PetFeatureFlag.Immortal)) {
            return null;
        }
        EntityStatMap entityStatMap = holder.getComponent(EntityStatMap.getComponentType());
        if (entityStatMap == null) {
            return null;
        }
        EntityStatValue statValue = entityStatMap.get(DefaultEntityStatTypes.getHealth());
        if (statValue == null) {
            return null;
        }
        int currentValue = MathUtil.floor(statValue.get());
        int maxValue = MathUtil.ceil(statValue.getMax());
        if (status == Status.DEAD) {
            return new PetUIHealthInfo(0.0f, 0, maxValue);
        }
        return new PetUIHealthInfo(statValue.asPercentage(), currentValue, maxValue);
    }
}
