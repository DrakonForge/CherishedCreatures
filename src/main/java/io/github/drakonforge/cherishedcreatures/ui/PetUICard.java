package io.github.drakonforge.cherishedcreatures.ui;

import au.ellie.hyui.builders.PageBuilder;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.asset.PetType.PetFeatureFlag;
import io.github.drakonforge.cherishedcreatures.component.PetBondComponent;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry.Status;
import io.github.drakonforge.cherishedcreatures.ui.PetUICard.PetUIBondingInfo.Type;
import io.github.drakonforge.cherishedcreatures.util.PetHelpers;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public record PetUICard(UUID id, String name, Status status, boolean isLoaded, boolean showSummonToggle, @Nullable PetUIHealthInfo healthInfo, @Nullable PetUIBondingInfo bondingInfo) {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    // TODO: Replace hardcoded values
    private static final float[] BONDING_XP_LEVEL_THRESHOLDS = { 0.0f, 150.0f, 300.0f, 450.0f, 600.0f };

    public record PetUIBondingInfo(Type type, float fillProgress, int bondingLevel) {
        public enum Type {
            FOUR_SEGMENT,
            LINEAR
        }
    }

    public record PetUIHealthInfo(float fillProgress, int value, int max) {}

    public static PetUICard fromTrackedPetEntry(TrackedPetEntry entry, Store<EntityStore> store) {
        entry.attemptSaveEntityFromLive(store);
        Holder<EntityStore> holder = entry.getHolder(false);
        PetType petType = entry.getPetType(store);

        String displayName = getDisplayName(holder);
        Status status = entry.getStatus();
        PetUIBondingInfo bondingInfo = getBondingInfo(petType, holder);
        PetUIHealthInfo healthInfo = getHealthInfo(petType, status, holder);
        boolean showSummonToggle = petType.hasFeatureFlag(PetFeatureFlag.SummonControls) && canSummonPetWithStatus(entry.getStatus());
        // If the pet has Status ALIVE but is unloaded, we basically treat it as un-summoned for our purposes.
        return new PetUICard(entry.getUuid(), displayName, status, entry.isLoaded(), showSummonToggle, healthInfo, bondingInfo);
    }

    private static boolean canSummonPetWithStatus(Status status) {
        return status == Status.STORED || status == Status.ALIVE;
    }

    private static String getDisplayName(Holder<EntityStore> holder) {
        // TODO: How is this different from Nameplate?
        DisplayNameComponent displayNameComponent = holder.getComponent(DisplayNameComponent.getComponentType());
        if (displayNameComponent != null) {
            Message displayName = displayNameComponent.getDisplayName();
            if (displayName != null) {
                return displayName.getAnsiMessage();
            }
        }
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
        // TODO: Replace with hardcoded values with server config, which can be overridden by pet type
        int bondingLevel = petBondComponent.getBondingLevel();
        float bondingXp = petBondComponent.getBondingXp();

        // Assume it is a 4-segment, this may change later
        if (0 <= bondingLevel && bondingLevel < BONDING_XP_LEVEL_THRESHOLDS.length - 1) {
            float xpRequiredForCurrentLevel = BONDING_XP_LEVEL_THRESHOLDS[bondingLevel];
            float xpRequiredForNextLevel = BONDING_XP_LEVEL_THRESHOLDS[bondingLevel + 1];
            float progressToNextLevel = (bondingXp - xpRequiredForCurrentLevel) / (xpRequiredForNextLevel - xpRequiredForCurrentLevel);
            float levelFillProgress = 0.25f * (bondingLevel + progressToNextLevel);
            float totalProgress = Math.clamp(levelFillProgress, 0.0f, 1.0f);
            return new PetUIBondingInfo(Type.FOUR_SEGMENT, totalProgress, bondingLevel);
        }
        return null;
    }

    @Nullable
    private static PetUIHealthInfo getHealthInfo(PetType petType, Status status, Holder<EntityStore> holder) {
        if (petType.hasFeatureFlag(PetFeatureFlag.Immortal) || status == Status.DEAD) {
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
        return new PetUIHealthInfo(statValue.asPercentage(), currentValue, maxValue);
    }

    private void refreshPetCard(Store<EntityStore> store, PlayerPetTracker petTracker, List<PetUICard> petCards, UUID petUuid) {
        int index = findPetCard(petCards, petUuid);
        if (index > -1) {
            TrackedPetEntry entry = petTracker.getPetEntry(petUuid);
            if (entry != null) {
                petCards.set(index, PetUICard.fromTrackedPetEntry(entry, store));
            }
        }
    }

    private int findPetCard(List<PetUICard> petCards, UUID petUuid) {
        for(int i = 0; i < petCards.size(); ++i) {
            if (petCards.get(i).id().equals(petUuid)) {
                return i;
            }
        }
        return -1;
    }

    public void registerEventListeners(PageBuilder builder, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, PlayerPetTracker petTracker, List<PetUICard> petCards) {
        if (showSummonToggle) {
            builder.addEventListener("toggle-summon-" + id, CustomUIEventBindingType.Activating, (data, ctx) -> {
                TrackedPetEntry entry = petTracker.getPetEntry(id);
                boolean hasChanged = false;
                if (entry == null) {
                    LOGGER.atWarning().log("Entry is null");
                    return;
                }
                if (entry.isLoaded()) {
                    LOGGER.atInfo().log("Calling unsummon pet from summon toggle");
                    if (PetHelpers.unsummonPet(entry, store)) {
                        playerRef.sendMessage(Message.raw("Unsummoned pet " + name + "!"));
                        hasChanged = true;
                    }
                } else {
                    TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
                    if (transformComponent == null) {
                        LOGGER.atWarning().log("Transform should not be null");
                        return;
                    }
                    PetHelpers.summonPet(entry, store, transformComponent);
                    playerRef.sendMessage(Message.raw("Summoned pet " + name + "!"));
                    hasChanged = true;
                }
                if (hasChanged) {
                    store.getExternalData().getWorld().execute(() -> {
                        refreshPetCard(store, petTracker, petCards, id);
                        ctx.updatePage(false);
                    });
                }
            });
        }
        if (status == Status.DEAD) {
            builder.addEventListener("accept-death-" + id, CustomUIEventBindingType.Activating, (data, ctx) -> {
                int index = findPetCard(petCards, id);
                if (index < 0 || petCards.get(index).status != Status.DEAD) {
                    playerRef.sendMessage(Message.raw("Pet does not exist or is not dead"));
                    return;
                }
                store.getExternalData().getWorld().execute(() -> {
                    boolean success = petTracker.removePetEntry(id);
                    if (success) {
                        petCards.remove(index);
                        playerRef.sendMessage(Message.raw("Accepted the death of " + name));
                        ctx.updatePage(true);
                    } else {
                        playerRef.sendMessage(Message.raw("Failed to remove pet"));
                    }
                });
            });
        }
    }
}
