package io.github.drakonforge.cherishedcreatures.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetEntityCommand;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PetAttributes;
import java.util.Collection;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class PetsAttributeCommand extends AbstractTargetEntityCommand {

    public PetsAttributeCommand() {
        super("attributes", "TODO");
        this.addAliases("attribute");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext,
            @NonNullDecl List<Ref<EntityStore>> objectList, @NonNullDecl World world,
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

        int numProcessed = 0;
        for (Ref<EntityStore> entityRef : objectList) {
            commandContext.sendMessage(Message.raw("Attributes for entity:"));
            PetAttributes petAttributes = store.getComponent(entityRef, PetAttributes.getComponentType());
            if (petAttributes == null) {
                continue;
            }
            Collection<String> keys = petAttributes.getKeys();
            for (String key : keys) {
                float value = petAttributes.getOrDefault(key, -1.0f);
                commandContext.sendMessage(Message.raw(key + " = " + value));
            }
            numProcessed += 1;
        }

        if (numProcessed <= 0) {
            commandContext.sendMessage(Message.raw("No target w/attributes found"));
        }
    }
}
