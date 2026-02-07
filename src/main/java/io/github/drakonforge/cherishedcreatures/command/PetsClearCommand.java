package io.github.drakonforge.cherishedcreatures.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class PetsClearCommand extends AbstractPlayerCommand {

    public PetsClearCommand() {
        super("clear", "TODO");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext,
            @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        PlayerPetTracker petTracker = store.getComponent(ref, PlayerPetTracker.getComponentType());
        if (petTracker == null) {
            commandContext.sendMessage(Message.raw("Pet tracker is null"));
            return;
        }
        petTracker.clearPetEntries();
        // TODO: Need to clear active pets too?
        commandContext.sendMessage(Message.raw("Cleared all pets"));
    }
}
