package io.github.drakonforge.cherishedcreatures.ui;

import au.ellie.hyui.builders.GroupBuilder;
import au.ellie.hyui.builders.LabelBuilder;
import au.ellie.hyui.builders.PageBuilder;
import au.ellie.hyui.html.TemplateProcessor;
import au.ellie.hyui.types.DefaultStyles;
import au.ellie.hyui.types.TextTooltipStyle;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
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
        List<TrackedPetEntry> trackedPetEntries = playerPetTracker.getPetEntries();
        for (int i = 0; i < trackedPetEntries.size(); i++) {
            TrackedPetEntry petEntry = trackedPetEntries.get(i);
            petCards.add(PetUICard.fromTrackedPetEntry(petEntry, store, false, i % 3));
        }

        TemplateProcessor template = new TemplateProcessor()
                .registerComponentFromFile("PetStatus", "Components/PetStatus.html")
                .registerComponentFromFile("CircularStatusMeter", "Components/CircularStatusMeter.html")
                .registerComponentFromFile("DestructiveButton", "Components/DestructiveButton.html")
                .setVariable("numPets", petCards.size())
                .setVariable("petCards", petCards);

        PageBuilder page = PageBuilder.detachedPage()
                .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction)
                .enableRuntimeTemplateUpdates(true)
                .loadHtml("Pages/PetMenuGrid.html", template);
        // The data-hyui tags don't seem to work properly, so add the scroll position manually
        page.getById("pet-card-list", GroupBuilder.class).ifPresent(builder -> {
            builder.withKeepScrollPosition(true);
        });

        for (PetUICard petUICard : petCards) {
            petUICard.registerMenuEventListeners(page, store, ref, playerRef, playerPetTracker, petCards);
        }
        page.open(playerRef, store);
    }

    public static boolean openPetDetails(@NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef, UUID petUuid) {
        PlayerPetTracker playerPetTracker = store.getComponent(ref, PlayerPetTracker.getComponentType());
        if (playerPetTracker == null) {
            LOGGER.atWarning().log("Pet tracker should not be null");
            return false;
        }

        TrackedPetEntry petEntry = playerPetTracker.getPetEntry(petUuid);
        if (petEntry == null) {
            LOGGER.atWarning().log("Cannot find entity " + petUuid + " for player " + playerRef.getUsername());
            return false;
        }

        PetUICard petCard = PetUICard.fromTrackedPetEntry(petEntry, store,true, -1);
        // Making a list so we can change the contents later
        List<PetUICard> petCardHolder = new ArrayList<>();
        petCardHolder.add(petCard);

        TemplateProcessor template = new TemplateProcessor()
                .registerComponentFromFile("PetStatus", "Components/PetStatus.html")
                .registerComponentFromFile("CircularStatusMeter", "Components/CircularStatusMeter.html")
                .registerComponentFromFile("DestructiveButton", "Components/DestructiveButton.html")
                .registerComponentFromFile("AttributeBar", "Components/AttributeBar.html")
                .setVariable("petCard", petCardHolder);

        PageBuilder page = PageBuilder.detachedPage()
                .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction)
                .enablePersistentElementEdits(true)
                .enableRuntimeTemplateUpdates(true)
                .loadHtml("Pages/PetDetails.html", template);

        String id = petCard.id().toString();

        // TODO: Neither of these work for some reason >:(
        page.getById("pet-name-" + id, LabelBuilder.class).ifPresent(builder -> {
            builder.withText("MyCustomText").withBackground("#ff0000").withTextTooltipStyle(
                    DefaultStyles.buttonTextTooltipStyle()).withTooltipText("MyText").withTooltipTextSpans(null);
            builder.withVisible(false);
            builder.addTextSpan(Message.raw("Hiiii"));
        });
        page.editById("pet-name-" + id, LabelBuilder.class, builder -> {
            builder.withVisible(false);
        });

        petCard.registerPetDetailsEventListeners(page, store, ref, playerRef, playerPetTracker, petCardHolder);

        page.open(playerRef, store);
        return true;
    }
}
