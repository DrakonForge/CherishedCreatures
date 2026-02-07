package io.github.drakonforge.cherishedcreatures.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

/**
 * This is an example command that will simply print the name of the plugin in chat when used.
 */
public class PetsCommand extends AbstractPlayerCommand {


    public PetsCommand() {
        super("pets", "server.commands.cherished_creatures.pets.desc");
        this.addAliases("pet");
        this.setPermissionGroup(GameMode.Adventure); // Allows the command to be used by anyone, not just OP
        this.addSubCommand(new PetsTameCommand());
        this.addSubCommand(new PetsSummonCommand());
        this.addSubCommand(new PetsUnsummonCommand());
        this.addSubCommand(new PetsClearCommand());
        this.addSubCommand(new PetsMenuCommand());
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
        commandContext.sendMessage(Message.raw("You currently have " + petTracker.getNumPetEntries() + " pet(s)"));
    }

}