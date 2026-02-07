package io.github.drakonforge.cherishedcreatures.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetEntityCommand;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.event.UpdatePetTrackerEvent;
import io.github.drakonforge.cherishedcreatures.util.PetHelpers;
import io.github.drakonforge.cherishedcreatures.util.PetHelpers.TameResult;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class PetsTameCommand extends AbstractTargetEntityCommand {

    public PetsTameCommand() {
        super("tame", "TODO");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext,
            @NonNullDecl ObjectList<Ref<EntityStore>> objectList, @NonNullDecl World world,
            @NonNullDecl Store<EntityStore> store) {
        if (!commandContext.isPlayer()) {
            commandContext.sendMessage(Message.raw("Must be player to run this command"));
            return;
        }
        Ref<EntityStore> ref = commandContext.senderAsPlayerRef();
        if (ref == null) {
            commandContext.sendMessage(Message.raw("Unable to retrieve player ref"));
            return;
        }

        commandContext.sendMessage(Message.raw("Found " + objectList.size() + " targeted entities"));
        int numAdded = 0;
        for (Ref<EntityStore> entityRef : objectList) {
            TameResult result = PetHelpers.attemptTame(store, ref, entityRef);
            if (result == TameResult.SUCCESS) {
                numAdded++;
            } else {
                commandContext.sendMessage(Message.raw("Taming failed with status " + result.name()));
            }
        }

        if (numAdded > 0) {
            commandContext.sendMessage(Message.raw("Added " + numAdded + " pets"));
            store.invoke(ref, new UpdatePetTrackerEvent());
        } else {
            commandContext.sendMessage(Message.raw("Failed to add any pets"));
        }
    }
}
