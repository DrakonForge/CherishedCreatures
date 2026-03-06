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
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.event.BondingXpEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class PetsBondCommand extends AbstractPlayerCommand {

    private final RequiredArg<Integer> indexArg;
    private final RequiredArg<Float> amountArg;

    public PetsBondCommand() {
        super("bond", "TODO");
        this.indexArg = this.withRequiredArg("index", "TODO", ArgTypes.INTEGER);
        this.amountArg = this.withRequiredArg("amount", "TODO", ArgTypes.FLOAT);
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
        Ref<EntityStore> petRef = entry.getEntityRef();
        float amount = commandContext.get(amountArg);
        if (petRef == null || !entry.isLoaded()) {
            commandContext.sendMessage(Message.raw("Pet is not loaded"));
            return;
        }
        store.invoke(petRef, new BondingXpEvent(amount));
    }
}
