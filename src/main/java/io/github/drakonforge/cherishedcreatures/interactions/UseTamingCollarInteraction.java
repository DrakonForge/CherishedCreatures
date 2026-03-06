package io.github.drakonforge.cherishedcreatures.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.UseEntityInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.util.PetHelpers;
import io.github.drakonforge.cherishedcreatures.util.PetHelpers.TameResult;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class UseTamingCollarInteraction extends SimpleInstantInteraction {

    @Nonnull
    public static final BuilderCodec<UseTamingCollarInteraction> CODEC = BuilderCodec.builder(
                    UseTamingCollarInteraction.class, UseTamingCollarInteraction::new, SimpleInstantInteraction.CODEC)
            .documentation("Attempts to use the tame entity, executing interactions on it if any.")
            .build();

    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Server;
    }

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType,
            @NonNullDecl InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();

        assert commandBuffer != null;

        World world = commandBuffer.getExternalData().getWorld();
        Store<EntityStore> store = world.getEntityStore().getStore();
        Ref<EntityStore> ref = context.getEntity();
        Ref<EntityStore> targetEntity = context.getTargetEntity();

        // TODO: ref is sometimes null

        TameResult result = PetHelpers.attemptTame(store, ref, targetEntity);
        if (result != TameResult.SUCCESS) {
            context.getState().state = InteractionState.Failed;
        }
    }

    public boolean needsRemoteSync() {
        return true;
    }

    @Nonnull
    public String toString() {
        return "UseTamingCollarInteraction{} " + super.toString();
    }
}
