package io.github.drakonforge.cherishedcreatures.ui.page;

import au.ellie.hyui.builders.GroupBuilder;
import au.ellie.hyui.builders.PageBuilder;
import au.ellie.hyui.builders.TextFieldBuilder;
import au.ellie.hyui.html.TemplateProcessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry.Status;
import io.github.drakonforge.cherishedcreatures.ui.data.PetMenuContext;
import io.github.drakonforge.cherishedcreatures.ui.data.PetUICard;
import io.github.drakonforge.cherishedcreatures.util.PetHelpers;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PetDetailsPage {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static boolean openPetDetails(PetMenuContext menuContext, UUID petUuid) {
        return openPetDetails(menuContext.store(), menuContext.ref(), menuContext.playerRef(),
                petUuid);
    }

    public static boolean openPetDetails(Store<EntityStore> store, Ref<EntityStore> ref,
            PlayerRef playerRef, UUID petUuid) {
        PlayerPetTracker playerPetTracker = store.getComponent(ref,
                PlayerPetTracker.getComponentType());
        if (playerPetTracker == null) {
            LOGGER.atWarning().log("Pet tracker should not be null");
            return false;
        }

        TrackedPetEntry petEntry = playerPetTracker.getPetEntry(petUuid);
        if (petEntry == null) {
            LOGGER.atWarning()
                    .log("Cannot find entity " + petUuid + " for player "
                            + playerRef.getUsername());
            return false;
        }

        PetUICard petCard = PetUICard.fromTrackedPetEntry(petEntry, store, true, -1);
        // Making a list so we can change the contents later
        List<PetUICard> petCardHolder = new ArrayList<>();
        petCardHolder.add(petCard);

        TemplateProcessor template = new TemplateProcessor().registerComponentFromFile("PetStatus",
                        "Components/PetStatus.html")
                .registerComponentFromFile("CircularStatusMeter",
                        "Components/CircularStatusMeter.html")
                .registerComponentFromFile("DestructiveButton", "Components/DestructiveButton.html")
                .registerComponentFromFile("AttributeBar", "Components/AttributeBar.html")
                .setVariable("petCard", petCardHolder);

        PageBuilder page = PageBuilder.detachedPage()
                .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction)
                .enablePersistentElementEdits(true)
                .enableRuntimeTemplateUpdates(true)
                .loadHtml("Pages/PetDetails.html", template);

        registerPetDetailsEventListeners(
                new PetMenuContext(page, store, ref, playerRef, playerPetTracker, petCardHolder,
                        petCard));
        page.open(playerRef, store);
        return true;
    }

    private static void registerPetDetailsEventListeners(PetMenuContext menuContext) {
        PageBuilder page = menuContext.page();
        PetUICard petCard = menuContext.petCard();

        page.addEventListener("back", CustomUIEventBindingType.Activating, (_, _) -> {
            PetMenuPage.openPetMenu(menuContext);
        });

        UUID id = petCard.id();
        Status status = petCard.status();

        PetCommonUI.addPetCardTooltips(menuContext);

        page.editById("change-name-container", GroupBuilder.class, changeBuilder -> {
            changeBuilder.withVisible(false);
        });

        if (status == Status.DEAD) {
            PetCommonUI.addAcceptDeathListener(menuContext);
            return;
        }

        addNameChangeListeners(menuContext);

        if (petCard.showSummonToggle()) {
            PetCommonUI.addSummonToggleListener(menuContext);
        }
    }

    private static void addNameChangeListeners(PetMenuContext menuContext) {
        PageBuilder page = menuContext.page();
        UUID id = menuContext.petCard().id();
        PlayerPetTracker petTracker = menuContext.petTracker();
        TrackedPetEntry entry = petTracker.getPetEntry(id);
        Store<EntityStore> store = menuContext.store();

        page.addEventListener("change-name-" + id, CustomUIEventBindingType.Activating,
                (_, ctx) -> {
                    ctx.editById("change-name-container", GroupBuilder.class, changeBuilder -> {
                        changeBuilder.withVisible(true);
                    });
                    ctx.editById("display-name-container", GroupBuilder.class, displayBuilder -> {
                        displayBuilder.withVisible(false);
                    });
                    ctx.updatePage(false);
                });

        page.addEventListener("change-name-submit-" + id, CustomUIEventBindingType.Activating,
                (_, ctx) -> {
                    if (entry == null) {
                        return;
                    }
                    ctx.getValue("change-name-input-" + id, String.class).ifPresent(newName -> {
                        newName = newName.trim();
                        if (newName.length() > PetCommonUI.MAX_PET_NAME_LENGTH) {
                            newName = newName.substring(0, PetCommonUI.MAX_PET_NAME_LENGTH);
                        }
                        if (!newName.isEmpty()) {
                            PetHelpers.renamePet(entry, store, newName);
                            PetCommonUI.refreshPetCard(menuContext, true);
                        }
                        // editById is not working at all for some reason
                        ctx.editById("change-name-input-" + id, TextFieldBuilder.class, builder -> {
                            builder.withValue("");
                        });
                    });
                    ctx.editById("change-name-container", GroupBuilder.class, changeBuilder -> {
                        changeBuilder.withVisible(false);
                    });
                    ctx.editById("display-name-container", GroupBuilder.class, displayBuilder -> {
                        displayBuilder.withVisible(true);
                    });
                    ctx.updatePage(false);
                });
    }

    private PetDetailsPage() {}
}
