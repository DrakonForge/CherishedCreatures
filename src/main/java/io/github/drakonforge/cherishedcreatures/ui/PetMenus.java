package io.github.drakonforge.cherishedcreatures.ui;

import au.ellie.hyui.builders.GroupBuilder;
import au.ellie.hyui.builders.PageBuilder;
import au.ellie.hyui.html.TemplateProcessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public final class PetMenus {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private PetMenus() {}

    public static void openMenu(@NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef) {
        PlayerPetTracker playerPetTracker = store.getComponent(ref, PlayerPetTracker.getComponentType());
        if (playerPetTracker == null) {
            LOGGER.atWarning().log("Pet tracker should not be null");
            return;
        }

        List<PetUICard> petCards = new ArrayList<>();
        for (TrackedPetEntry petEntry : playerPetTracker.getPetEntries()) {
            petCards.add(PetUICard.fromTrackedPetEntry(petEntry, store));
        }

        TemplateProcessor template = new TemplateProcessor()
                .registerComponentFromFile("PetStatus", "Components/PetStatus.html")
                .registerComponentFromFile("CircularStatusMeter", "Components/CircularStatusMeter.html")
                .setVariable("numPets", petCards.size())
                .setVariable("petCards", petCards);

        PageBuilder builder = PageBuilder.detachedPage()
                .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction)
                .enableRuntimeTemplateUpdates(true)
                .loadHtml("Pages/PetMenu.html", template);
        // The data-hyui tags don't seem to work properly, so add the scroll position manually
        builder.getById("pet-card-list", GroupBuilder.class).ifPresent(groupBuilder -> {
            groupBuilder.withKeepScrollPosition(true);
        });
        for (PetUICard petUICard : petCards) {
            petUICard.registerMenuEventListeners(builder, store, ref, playerRef, playerPetTracker, petCards);
        }
        builder.open(playerRef, store);
    }

    public static void openPetDetails(@NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef, UUID petUuid) {
        PlayerPetTracker playerPetTracker = store.getComponent(ref, PlayerPetTracker.getComponentType());
        if (playerPetTracker == null) {
            LOGGER.atWarning().log("Pet tracker should not be null");
            return;
        }

        TrackedPetEntry petEntry = playerPetTracker.getPetEntry(petUuid);
        if (petEntry == null) {
            LOGGER.atWarning().log("Cannot find entity " + petUuid + " for player " + playerRef.getUsername());
            return;
        }

        PetUICard petCard = PetUICard.fromTrackedPetEntry(petEntry, store);
        // Making a list so we can change the contents later
        List<PetUICard> petCardHolder = new ArrayList<>();
        petCardHolder.add(petCard);

        TemplateProcessor template = new TemplateProcessor()
                .registerComponentFromFile("PetStatus", "Components/PetStatus.html")
                .registerComponentFromFile("CircularStatusMeter", "Components/CircularStatusMeter.html")
                .setVariable("petCard", petCardHolder);

        PageBuilder builder = PageBuilder.detachedPage()
                .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction)
                .enableRuntimeTemplateUpdates(true)
                .loadHtml("Pages/PetDetails.html", template);
        // The data-hyui tags don't seem to work properly, so add the scroll position manually
        builder.getById("pet-card-list", GroupBuilder.class).ifPresent(groupBuilder -> {
            groupBuilder.withKeepScrollPosition(true);
        });


        petCard.registerPetDetailsEventListeners(builder, store, ref, playerRef, playerPetTracker, petCardHolder);

        builder.open(playerRef, store);
    }
}
