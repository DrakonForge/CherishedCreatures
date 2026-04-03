package io.github.drakonforge.cherishedcreatures.ui.page;

import au.ellie.hyui.builders.PageBuilder;
import au.ellie.hyui.html.TemplateProcessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.ui.data.PetMenuContext;
import io.github.drakonforge.cherishedcreatures.ui.data.PetUICard;
import io.github.drakonforge.cherishedcreatures.util.PetHelpers;
import io.github.drakonforge.cherishedcreatures.util.PetHelpers.UntameResult;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PetAbandonConfirmPage {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static boolean openPetAbandonConfirm(PetMenuContext menuContext, UUID petUuid) {
        Store<EntityStore> store = menuContext.store();
        Ref<EntityStore> ref = menuContext.ref();
        PlayerRef playerRef = menuContext.playerRef();

        PlayerPetTracker playerPetTracker = store.getComponent(ref,
                PlayerPetTracker.getComponentType());
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        assert transformComponent != null;
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

        Vector3d origin = transformComponent.getPosition().clone();
        PetUICard petCard = PetUICard.fromTrackedPetEntry(petEntry, store, origin, true, -1);
        // Making a list so we can change the contents later
        List<PetUICard> petCardHolder = new ArrayList<>();
        petCardHolder.add(petCard);

        TemplateProcessor template = new TemplateProcessor()
                .registerComponentFromFile("DestructiveButton", "Components/DestructiveButton.html")
                .setVariable("petCard", petCardHolder);

        PageBuilder page = PageBuilder.detachedPage()
                .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction)
                .loadHtml("Pages/PetMenuAbandonConfirm.html", template);
        registerPetAbandonConfirmListeners(new PetMenuContext(page, menuContext.store(), menuContext.ref(), menuContext.playerRef(), menuContext.petTracker(), menuContext.petCards(), menuContext.petCard(), menuContext.origin()));
        page.open(playerRef, store);
        return true;
    }

    private static void registerPetAbandonConfirmListeners(PetMenuContext menuContext) {
        PageBuilder page = menuContext.page();
        UUID id = menuContext.petCard().id();

        page.addEventListener("cancel-abandon", CustomUIEventBindingType.Activating, (_, _) -> {
            PetDetailsPage.openPetDetails(menuContext, id);
        });

        page.addEventListener("confirm-abandon", CustomUIEventBindingType.Activating, (_, _) -> {
            LOGGER.atInfo().log("Confirm abandon");
            PlayerPetTracker tracker = menuContext.petTracker();
            TrackedPetEntry entry = tracker.getPetEntry(id);
            if (entry == null) {
                LOGGER.atWarning().log("Unable to find pet entry to remove");
            } else {
                UntameResult result = PetHelpers.attemptUntame(menuContext.store(), tracker, entry);
                if (result != UntameResult.SUCCESS) {
                    LOGGER.atWarning().log("Failed to untame pet with status " + result.name());
                } else {
                    // TODO: Send message to player? And fail cases too?
                    LOGGER.atInfo().log("Untamed pet successfully");
                }
            }
            // Always open menu at the end
            PetMenuPage.openPetMenu(menuContext);
        });
    }
}
