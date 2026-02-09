package io.github.drakonforge.cherishedcreatures.ui;

import au.ellie.hyui.builders.PageBuilder;
import au.ellie.hyui.html.TemplateProcessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public final class PetMenu {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private PetMenu() {}

    public static void openForPlayer( @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
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
                .setVariable("numPets", petCards.size())
                .setVariable("petCards", petCards);

        PageBuilder builder = PageBuilder.detachedPage()
                .loadHtml("Pages/PetMenu.html", template);
        for (PetUICard petUICard : petCards) {
            petUICard.registerEventListeners(builder, store, ref, playerRef, playerPetTracker);
        }
        builder.open(playerRef, store);
    }
}
