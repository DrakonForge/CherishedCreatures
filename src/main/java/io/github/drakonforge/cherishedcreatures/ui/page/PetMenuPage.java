package io.github.drakonforge.cherishedcreatures.ui.page;

import au.ellie.hyui.builders.GroupBuilder;
import au.ellie.hyui.builders.PageBuilder;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public final class PetMenuPage {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static void openPetMenu(PetMenuContext menuContext) {
        openPetMenu(menuContext.store(), menuContext.ref(), menuContext.playerRef());
    }

    public static void openPetMenu(@NonNullDecl Store<EntityStore> store,
            @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef) {
        PlayerPetTracker playerPetTracker = store.getComponent(ref,
                PlayerPetTracker.getComponentType());
        if (playerPetTracker == null) {
            LOGGER.atWarning().log("Pet tracker should not be null");
            return;
        }

        List<PetUICard> petCards = new ArrayList<>();
        List<TrackedPetEntry> trackedPetEntries = playerPetTracker.getPetEntries();
        for (int i = 0; i < trackedPetEntries.size(); i++) {
            TrackedPetEntry petEntry = trackedPetEntries.get(i);
            petCards.add(PetUICard.fromTrackedPetEntry(petEntry, store, false, i % 3));
        }

        TemplateProcessor template = new TemplateProcessor().registerComponentFromFile("PetStatus",
                        "Components/PetStatus.html")
                .registerComponentFromFile("CircularStatusMeter",
                        "Components/CircularStatusMeter.html")
                .registerComponentFromFile("DestructiveButton", "Components/DestructiveButton.html")
                .setVariable("numPets", petCards.size())
                .setVariable("petCards", petCards);

        PageBuilder page = PageBuilder.detachedPage()
                .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction)
                .enablePersistentElementEdits(true)
                .enableRuntimeTemplateUpdates(true)
                .loadHtml("Pages/PetMenuGrid.html", template);
        // The data-hyui tags don't seem to work properly, so add the scroll position manually
        page.editById("pet-card-list", GroupBuilder.class, builder -> {
            builder.withKeepScrollPosition(true);
        });

        for (PetUICard petCard : petCards) {
            registerMenuEventListeners(
                    new PetMenuContext(page, store, ref, playerRef, playerPetTracker, petCards,
                            petCard));
        }
        page.open(playerRef, store);
    }

    private static void registerMenuEventListeners(PetMenuContext menuContext) {
        PageBuilder page = menuContext.page();
        PetUICard petCard = menuContext.petCard();
        PlayerPetTracker petTracker = menuContext.petTracker();
        UUID id = petCard.id();
        Status status = petCard.status();

        PetCommonUI.addPetCardTooltips(menuContext);

        if (petCard.showSummonToggle()) {
            PetCommonUI.addSummonToggleListener(menuContext);
        }
        if (status == Status.DEAD) {
            PetCommonUI.addAcceptDeathListener(menuContext);
        }

        page.addEventListener("view-details-" + id, CustomUIEventBindingType.Activating,
                (_, ctx) -> {
                    TrackedPetEntry entry = petTracker.getPetEntry(id);
                    if (entry == null) {
                        LOGGER.atWarning().log("Entry is null");
                        return;
                    }
                    PetDetailsPage.openPetDetails(menuContext, entry.getUuid());
                });
    }

    private PetMenuPage() {}
}
