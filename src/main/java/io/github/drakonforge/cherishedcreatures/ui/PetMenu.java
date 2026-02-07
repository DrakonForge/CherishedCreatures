package io.github.drakonforge.cherishedcreatures.ui;

import au.ellie.hyui.builders.PageBuilder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public final class PetMenu {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private PetMenu() {}

    private static final String HTML = """
        <div class="page-overlay">
            <div class="container" data-hyui-title="Pets">
                <div class="container-contents">
                    <p>Welcome to the menu!</p>
                    <button id="myBtn">Click Me</button>
                    <img class="dynamic-image" src="https://example.invalid/render/PlayerName" />
                </div>
            </div>
        </div>
    """;

    public static void openForPlayer( @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef) {
        PlayerPetTracker playerPetTracker = store.getComponent(ref, PlayerPetTracker.getComponentType());
        if (playerPetTracker == null) {
            LOGGER.atWarning().log("Pet tracker should not be null");
            return;
        }
        PageBuilder.pageForPlayer(playerRef)
                .fromHtml(HTML)
                .addEventListener("myBtn", CustomUIEventBindingType.Activating, (ctx) -> {
                    playerRef.sendMessage(Message.raw("Clicked!"));
                })
                .open(store);
    }
}
