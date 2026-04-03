package io.github.drakonforge.cherishedcreatures.ui.page;

import au.ellie.hyui.builders.GroupBuilder;
import au.ellie.hyui.builders.LabelBuilder;
import au.ellie.hyui.builders.PageBuilder;
import au.ellie.hyui.types.DefaultStyles;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.component.PetStateComponent;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.component.PlayerUIPreferencesComponent;
import io.github.drakonforge.cherishedcreatures.data.PetFollowMode;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry.Status;
import io.github.drakonforge.cherishedcreatures.ui.data.PetMenuContext;
import io.github.drakonforge.cherishedcreatures.ui.data.PetUICard;
import io.github.drakonforge.cherishedcreatures.util.PetHelpers;
import org.jline.utils.Log;

import java.util.List;
import java.util.UUID;

public final class PetCommonUI {

    public static final int MAX_PET_NAME_LENGTH = 16;
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static void addPetCardTooltips(PetMenuContext menuContext) {
        PageBuilder page = menuContext.page();
        PetUICard petCard = menuContext.petCard();
        UUID id = petCard.id();
        page.editById("pet-name-" + id, LabelBuilder.class, builder -> {
            builder.withTextTooltipStyle(DefaultStyles.buttonTextTooltipStyle())
                    .withTooltipText(id.toString());
        });
        if (petCard.bondingInfo() != null) {
            page.editById("bonding-meter-" + id, GroupBuilder.class, builder -> {
                builder.withTextTooltipStyle(DefaultStyles.buttonTextTooltipStyle())
                        .withTooltipText("Bonding Level " + petCard.bondingInfo().bondingLevel());
            });
        }
        if (petCard.healthInfo() != null) {
            page.editById("health-meter-" + id, GroupBuilder.class, builder -> {
                builder.withTextTooltipStyle(DefaultStyles.buttonTextTooltipStyle())
                        .withTooltipTextSpans(List.of(Message.raw("Health: "), Message.raw(
                                        petCard.healthInfo().value() + "/" + petCard.healthInfo().max())
                                .bold(true)));
            });
        }
    }

    public static void refreshPetCard(PetMenuContext menuContext, boolean generateDetails) {
        Store<EntityStore> store = menuContext.store();
        PlayerPetTracker petTracker = menuContext.petTracker();
        List<PetUICard> petCards = menuContext.petCards();
        UUID petUuid = menuContext.petCard().id();
        Vector3d origin = menuContext.origin();
        refreshPetCard(store, petTracker, petCards, origin, generateDetails, petUuid);
    }

    private static void refreshPetCard(Store<EntityStore> store, PlayerPetTracker petTracker,
            List<PetUICard> petCards, Vector3d origin, boolean generateDetails, UUID petUuid) {
        int index = findPetCard(petCards, petUuid);
        if (index > -1) {
            TrackedPetEntry entry = petTracker.getPetEntry(petUuid);
            if (entry != null) {
                petCards.set(index,
                        PetUICard.fromTrackedPetEntry(entry, store, origin, generateDetails, index));
            }
        }
    }

    private static int findPetCard(List<PetUICard> petCards, UUID petUuid) {
        for (int i = 0; i < petCards.size(); ++i) {
            if (petCards.get(i).id().equals(petUuid)) {
                return i;
            }
        }
        return -1;
    }

    private static final String VALUE_STAY = "Stay";
    private static final String VALUE_FOLLOW = "Follow";

    public static void addStayFollowToggleListener(PetMenuContext menuContext) {
        // TODO: Set initial value
        PetUICard petCard = menuContext.petCard();
        PlayerPetTracker petTracker = menuContext.petTracker();
        UUID id = petCard.id();
        menuContext.page().addEventListener("follow-mode-select-" + id, CustomUIEventBindingType.ValueChanged, (_, ctx) -> {
            ctx.getValue("follow-mode-select-" + id, String.class).ifPresent(mode -> {
                PetFollowMode followMode;
                if (mode.equals(VALUE_STAY)) {
                    LOGGER.atInfo().log("Set to stay");
                    followMode = PetFollowMode.Stay;
                } else if (mode.equals(VALUE_FOLLOW)) {
                    LOGGER.atInfo().log("Set to follow");
                    followMode = PetFollowMode.Follow;
                } else {
                    LOGGER.atWarning().log("Unrecognized follow mode: " + mode);
                    return;
                }
                TrackedPetEntry entry = petTracker.getPetEntry(id);
                if (entry == null) {
                    LOGGER.atWarning().log("Entry is null");
                    return;
                }
                Holder<EntityStore> holder = entry.getHolder(false);
                PetStateComponent petStateComponent = holder.getComponent(PetStateComponent.getComponentType());
                if (petStateComponent == null) {
                    LOGGER.atWarning().log("Pet state component should not be null");
                    return;
                }
                petStateComponent.setFollowMode(followMode);

            });
        });

        // TODO: Roaming Radius checks
    }

    public static void addSummonToggleListener(PetMenuContext menuContext) {
        PetUICard petCard = menuContext.petCard();
        PlayerRef playerRef = menuContext.playerRef();
        PlayerPetTracker petTracker = menuContext.petTracker();
        Store<EntityStore> store = menuContext.store();
        UUID id = petCard.id();
        String name = petCard.name();
        menuContext.page()
                .addEventListener("toggle-summon-" + id, CustomUIEventBindingType.Activating,
                        (_, ctx) -> {
                            TrackedPetEntry entry = petTracker.getPetEntry(id);
                            boolean hasChanged = false;
                            if (entry == null) {
                                LOGGER.atWarning().log("Entry is null");
                                return;
                            }
                            if (entry.isLoaded()) {
                                LOGGER.atInfo()
                                        .log("Calling unsummon pet from summon toggle");
                                if (PetHelpers.unsummonPet(entry, store)) {
                                    playerRef.sendMessage(
                                            Message.raw("Unsummoned pet " + name + "!"));
                                    hasChanged = true;
                                }
                            } else {
                                TransformComponent transformComponent = store.getComponent(
                                        menuContext.ref(), TransformComponent.getComponentType());
                                if (transformComponent == null) {
                                    LOGGER.atWarning()
                                            .log("Transform should not be null");
                                    return;
                                }
                                PetHelpers.summonPet(entry, store, transformComponent);
                                playerRef.sendMessage(Message.raw("Summoned pet " + name + "!"));
                                hasChanged = true;
                            }
                            if (hasChanged) {
                                store.getExternalData().getWorld().execute(() -> {
                                    refreshPetCard(menuContext, false);
                                    ctx.updatePage(true);
                                });
                            }
                        });
    }

    public static void addAcceptDeathListener(PetMenuContext menuContext) {
        PetUICard petCard = menuContext.petCard();
        List<PetUICard> petCards = menuContext.petCards();
        PlayerRef playerRef = menuContext.playerRef();
        PlayerPetTracker petTracker = menuContext.petTracker();
        UUID id = petCard.id();
        String name = petCard.name();
        menuContext.page()
                .addEventListener("accept-death-" + id, CustomUIEventBindingType.Activating,
                        (_, ctx) -> {
                            int index = findPetCard(petCards, id);
                            if (index < 0 || petCards.get(index).status() != Status.DEAD) {
                                playerRef.sendMessage(
                                        Message.raw("Pet does not exist or is not dead"));
                                return;
                            }
                            menuContext.store().getExternalData().getWorld().execute(() -> {
                                boolean success = petTracker.removePetEntry(id);
                                if (success) {
                                    petCards.remove(index);
                                    playerRef.sendMessage(
                                            Message.raw("Accepted the death of " + name));
                                    ctx.updatePage(true);
                                } else {
                                    playerRef.sendMessage(Message.raw("Failed to remove pet"));
                                }
                            });
                        });
    }

    public static void addAbandonListener(PetMenuContext menuContext) {
        UUID id = menuContext.petCard().id();
        menuContext.page().addEventListener("abandon-pet-" + id, CustomUIEventBindingType.Activating, (_, _) -> {
            PetAbandonConfirmPage.openPetAbandonConfirm(menuContext, id);
        });
    }
    public static void addToggleUILayoutPreference(Ref<EntityStore> ref, PageBuilder page, Store<EntityStore> store) {
        // Invalidate current UI and rerender
        page
           .addEventListener("toggle-layout", CustomUIEventBindingType.Activating, (_, ctx) -> {
               PlayerUIPreferencesComponent playerUIPreferencesComponent = store.getComponent(ref, PlayerUIPreferencesComponent.getComponentType());
               assert playerUIPreferencesComponent != null;

               Log.info("CHANGING UI PREFERENCE");
               playerUIPreferencesComponent.toggleLayoutPreference();
           });
    }

    private PetCommonUI() {}

}
