package io.github.drakonforge.cherishedcreatures.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.asset.PetType.AbandonBehavior;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry.Status;
import io.github.drakonforge.cherishedcreatures.util.PetHelpers;
import io.github.drakonforge.cherishedcreatures.util.PetHelpers.UntameResult;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class PetsUntameCommand extends AbstractPlayerCommand {

    private final RequiredArg<Integer> indexArg;

    public PetsUntameCommand() {
        super("untame", "TODO");
        this.addAliases("abandon");
        this.indexArg = this.withRequiredArg("index", "TODO", ArgTypes.INTEGER);
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext,
            @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        PlayerPetTracker petTracker = store.getComponent(ref, PlayerPetTracker.getComponentType());
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (petTracker == null) {
            commandContext.sendMessage(Message.raw("Pet tracker is null"));
            return;
        }
        if (transformComponent == null) {
            commandContext.sendMessage(Message.raw("Transform is null"));
            return;
        }

        int index = commandContext.get(indexArg);
        if (index < 0 || index >= petTracker.getNumPetEntries()) {
            commandContext.sendMessage(Message.raw("Index out of bounds"));
            return;
        }

        TrackedPetEntry entry = petTracker.getPetEntry(index);
        UntameResult result = PetHelpers.attemptUntame(store, petTracker, entry);
        if (result == UntameResult.SUCCESS) {
            commandContext.sendMessage(Message.raw("Successfully untamed"));
        } else {
            commandContext.sendMessage(Message.raw("Untame failed with status " + result.name()));
        }
    }
}
