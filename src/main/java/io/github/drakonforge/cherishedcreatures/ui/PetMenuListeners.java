package io.github.drakonforge.cherishedcreatures.ui;

import au.ellie.hyui.builders.PageBuilder;
import com.hypixel.hytale.component.Ref;
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
import io.github.drakonforge.cherishedcreatures.util.PetHelpers;
import java.util.List;
import java.util.UUID;

public final class PetMenuListeners {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public record PetMenuContext(PageBuilder page, Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef, PlayerPetTracker petTracker, List<PetUICard> petCards, PetUICard petCard) {}

    public static void registerMenuEventListeners(PetMenuContext menuContext) {
        PageBuilder page = menuContext.page();
        PetUICard petCard = menuContext.petCard();
        PlayerPetTracker petTracker = menuContext.petTracker();
        UUID id = petCard.id();
        Status status = petCard.status();

        if (petCard.showSummonToggle()) {
            addSummonToggleListener(menuContext);
        }
        if (status == Status.DEAD) {
            addAcceptDeathListener(menuContext);
        }

        page.addEventListener("view-details-" + id, CustomUIEventBindingType.Activating, (_, ctx) -> {
            TrackedPetEntry entry = petTracker.getPetEntry(id);
            if (entry == null) {
                LOGGER.atWarning().log("Entry is null");
                return;
            }
            PetMenus.openPetDetails(menuContext, entry.getUuid());
        });
    }

    public static void registerPetDetailsEventListeners(
            PetMenuContext menuContext) {
        PageBuilder page = menuContext.page();
        PetUICard petCard = menuContext.petCard();

        page.addEventListener("back", CustomUIEventBindingType.Activating, (_, _) -> {
            PetMenus.openPetMenu(menuContext);
        });

        UUID id = petCard.id();
        Status status = petCard.status();

        if (status == Status.DEAD) {
            addAcceptDeathListener(menuContext);
        }

        // builder.addEventListener("change-name-" + id, CustomUIEventBindingType.Activating, (_, ctx) -> {
        //     LOGGER.atInfo().log("Called");
        //     // TODO: Restore IDs if possible
        //     ctx.getById("change-name-container", GroupBuilder.class).ifPresent(changeBuilder -> {
        //         changeBuilder.withVisible(true);
        //         ctx.updatePage(true);
        //     });
        //     ctx.getById("display-name-container", GroupBuilder.class).ifPresent(displayBuilder -> {
        //         displayBuilder.withVisible(false);
        //         ctx.updatePage(true);
        //     });
        //
        //     LOGGER.atWarning().log("Exiting");
        //
        //     Optional<GroupBuilder> changeBuilder = ctx.getById("change-name-container", GroupBuilder.class);
        //     Optional<GroupBuilder> displayBuilder = ctx.getById("display-name-container", GroupBuilder.class);
        //
        //     if (changeBuilder.isPresent() && displayBuilder.isPresent()) {
        //         LOGGER.atInfo().log("Success");
        //         changeBuilder.get().withVisible(true);
        //         displayBuilder.get().withVisible(false);
        //         ctx.updatePage(true);
        //     } else {
        //         LOGGER.atWarning().log("Fail");
        //     }
        //     LOGGER.atWarning().log("Exiting");
        // });

        page.addEventListener("change-name-submit-" + id, CustomUIEventBindingType.Activating, (data, ctx) -> {
            LOGGER.atInfo().log("Called");
            // Optional<GroupBuilder> changeBuilder = ctx.getById("change-name-container", GroupBuilder.class);
            // Optional<GroupBuilder> displayBuilder = ctx.getById("display-name-container", GroupBuilder.class);

            // if (changeBuilder.isPresent() && displayBuilder.isPresent()) {
            //     LOGGER.atInfo().log("Success");
            //     changeBuilder.get().withVisible(true);
            //     displayBuilder.get().withVisible(false);
            //     ctx.updatePage(true);
            // } else {
            //     LOGGER.atWarning().log("Fail");
            // }
            // TODO: Change name
        });

        if (petCard.showSummonToggle()) {
            addSummonToggleListener(menuContext);
        }
    }

    private static void refreshPetCard(
            Store<EntityStore> store, PlayerPetTracker petTracker, List<PetUICard> petCards, boolean generateDetails, UUID petUuid) {
        int index = findPetCard(petCards, petUuid);
        if (index > -1) {
            TrackedPetEntry entry = petTracker.getPetEntry(petUuid);
            if (entry != null) {
                petCards.set(index, PetUICard.fromTrackedPetEntry(entry, store, generateDetails, index));
            }
        }
    }

    private static int findPetCard(List<PetUICard> petCards, UUID petUuid) {
        for(int i = 0; i < petCards.size(); ++i) {
            if (petCards.get(i).id().equals(petUuid)) {
                return i;
            }
        }
        return -1;
    }

    private static void addSummonToggleListener(PetMenuContext menuContext) {
        PetUICard petCard = menuContext.petCard();
        List<PetUICard> petCards = menuContext.petCards();
        PlayerRef playerRef = menuContext.playerRef();
        PlayerPetTracker petTracker = menuContext.petTracker();
        Store<EntityStore> store = menuContext.store();
        UUID id = petCard.id();
        String name = petCard.name();
        menuContext.page().addEventListener("toggle-summon-" + id, CustomUIEventBindingType.Activating, (_, ctx) -> {
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
                TransformComponent transformComponent = store.getComponent(menuContext.ref(), TransformComponent.getComponentType());
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
                    refreshPetCard(store, petTracker, petCards, false, id);
                    ctx.updatePage(false);
                });
            }
        });
    }

    private static void addAcceptDeathListener(PetMenuContext menuContext) {
        PetUICard petCard = menuContext.petCard();
        List<PetUICard> petCards = menuContext.petCards();
        PlayerRef playerRef = menuContext.playerRef();
        PlayerPetTracker petTracker = menuContext.petTracker();
        UUID id = petCard.id();
        String name = petCard.name();
        menuContext.page().addEventListener("accept-death-" + id, CustomUIEventBindingType.Activating, (_, ctx) -> {
            int index = findPetCard(petCards, id);
            if (index < 0 || petCards.get(index).status() != Status.DEAD) {
                playerRef.sendMessage(Message.raw("Pet does not exist or is not dead"));
                return;
            }
            menuContext.store().getExternalData().getWorld().execute(() -> {
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
