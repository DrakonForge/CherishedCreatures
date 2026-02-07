package io.github.drakonforge.cherishedcreatures.command;

import au.ellie.hyui.builders.PageBuilder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class PetsMenuCommand extends AbstractPlayerCommand {

    String html = """
        <div class="page-overlay">
            <div class="container" data-hyui-title="My Menu">
                <div class="container-contents">
                    <p>Welcome to the menu!</p>
                    <button id="myBtn">Click Me</button>
                    <img class="dynamic-image" src="https://example.invalid/render/PlayerName" />
                </div>
            </div>
        </div>
    """;

    public PetsMenuCommand() {
        super("menu", "TODO");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext,
            @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        PageBuilder.pageForPlayer(playerRef)
                .fromHtml(html)
                .addEventListener("myBtn", CustomUIEventBindingType.Activating, (ctx) -> {
                    playerRef.sendMessage(Message.raw("Clicked!"));
                })
                .open(store);
    }
}
