package io.github.drakonforge.cherishedcreatures.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetEntityCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PetBondComponent;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import io.github.drakonforge.cherishedcreatures.data.BondingActivityType;
import io.github.drakonforge.cherishedcreatures.event.TriggerBondingActivityEvent;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ApplyPetCommand extends AbstractTargetEntityCommand {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public ApplyPetCommand() {
        super("pet", "Pet the pet you are currently looking at, bonding with it.");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext,
                           @NonNullDecl ObjectList<Ref<EntityStore>> objectList,
                           @NonNullDecl World world,
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
        UUIDComponent playerUuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
        if (playerUuidComponent == null) {
            return;
        }
        for (Ref<EntityStore> entityRef : objectList) {
            PetBondComponent petBondComponent = store.getComponent(entityRef,PetBondComponent.getComponentType());
            PetComponent petComponent = store.getComponent(entityRef, PetComponent.getComponentType());
            PetTypeComponent petTypeComponent = store.getComponent(entityRef, PetTypeComponent.getComponentType());


            if (petTypeComponent == null || petBondComponent == null || petComponent == null) {
                continue;
            }

            if (!playerUuidComponent.getUuid().equals(petComponent.getOwnerUuid())) {
                commandContext.sendMessage(Message.raw("Attempted to pet an unowned pet"));
                continue;
            }

            store.invoke(entityRef, new TriggerBondingActivityEvent(BondingActivityType.Petting));
            commandContext.sendMessage(Message.raw("Successfully applied pet"));
        }
    }


}
