package io.github.drakonforge.cherishedcreatures.ui.page;

import au.ellie.hyui.builders.GroupBuilder;
import au.ellie.hyui.builders.LabelBuilder;
import au.ellie.hyui.builders.PageBuilder;
import au.ellie.hyui.types.DefaultStyles;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry.Status;
import io.github.drakonforge.cherishedcreatures.ui.data.PetMenuContext;
import io.github.drakonforge.cherishedcreatures.ui.data.PetUICard;
import io.github.drakonforge.cherishedcreatures.util.PetHelpers;
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
        refreshPetCard(store, petTracker, petCards, generateDetails, petUuid);
    }

    private static void refreshPetCard(Store<EntityStore> store, PlayerPetTracker petTracker,
            List<PetUICard> petCards, boolean generateDetails, UUID petUuid) {
        int index = findPetCard(petCards, petUuid);
        if (index > -1) {
            TrackedPetEntry entry = petTracker.getPetEntry(petUuid);
            if (entry != null) {
                petCards.set(index,
                        PetUICard.fromTrackedPetEntry(entry, store, generateDetails, index));
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

    private PetCommonUI() {}

}
