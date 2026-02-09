package io.github.drakonforge.cherishedcreatures.ui;

import au.ellie.hyui.builders.ButtonBuilder;
import au.ellie.hyui.builders.PageBuilder;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.asset.PetType.PetFeatureFlag;
import io.github.drakonforge.cherishedcreatures.component.PetBondComponent;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.ui.PetUICard.PetUIBondingInfo.Type;
import io.github.drakonforge.cherishedcreatures.util.PetHelpers;
import java.util.UUID;
import javax.annotation.Nullable;

public record PetUICard(UUID id, String name, boolean isLoaded, boolean showSummonToggle, @Nullable PetUIBondingInfo bondingInfo) {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    // TODO: Replace hardcoded values
    private static final float[] BONDING_XP_LEVEL_THRESHOLDS = { 0.0f, 150.0f, 300.0f, 450.0f, 600.0f };

    public record PetUIBondingInfo(Type type, float fillProgress) {
        public enum Type {
            FOUR_SEGMENT,
            LINEAR
        }
    }

    public static PetUICard fromTrackedPetEntry(TrackedPetEntry entry, Store<EntityStore> store) {
        Holder<EntityStore> holder = entry.updateAndGetHolder(store);
        PetType petType = entry.getPetType(store);

        String displayName = getDisplayName(holder);
        PetUIBondingInfo bondingInfo = getBondingInfo(holder);

        return new PetUICard(entry.getUuid(), displayName, entry.isLoaded(), petType.hasFeatureFlag(
                PetFeatureFlag.SummonControls), bondingInfo);

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
    private static PetUIBondingInfo getBondingInfo(Holder<EntityStore> holder) {
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
            return new PetUIBondingInfo(Type.FOUR_SEGMENT, totalProgress);
        }
        return null;
    }

    public void registerEventListeners(PageBuilder builder, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, PlayerPetTracker petTracker) {
        if (showSummonToggle) {
            builder.addEventListener("toggle-summon-" + id, CustomUIEventBindingType.Activating, (data, ctx) -> {
                TrackedPetEntry entry = petTracker.getPetEntry(id);
                if (entry.isLoaded()) {
                    if (PetHelpers.unsummonPet(entry, store)) {
                        playerRef.sendMessage(Message.raw("Unsummoned pet " + name + "!"));
                    }
                    ctx.getById("toggle-summon-" + id, ButtonBuilder.class).ifPresent(buttonBuilder -> {
                        buttonBuilder.withText("Summon");
                        ctx.updatePage(false);
                    });
                } else {
                    TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
                    if (transformComponent == null) {
                        LOGGER.atWarning().log("Transform should not be null");
                        return;
                    }
                    PetHelpers.summonPet(entry, store, transformComponent);
                    playerRef.sendMessage(Message.raw("Summoned pet " + name + "!"));
                    ctx.getById("toggle-summon-" + id, ButtonBuilder.class).ifPresent(buttonBuilder -> {
                        buttonBuilder.withText("Unsummon");
                        ctx.updatePage(false);
                    });
                }
            });
        }
    }
}
