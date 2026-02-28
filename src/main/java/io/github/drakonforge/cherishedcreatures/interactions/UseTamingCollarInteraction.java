package io.github.drakonforge.cherishedcreatures.interactions;


import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.UseEntityInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

public class UseTamingCollarInteraction extends SimpleInstantInteraction {
    @Nonnull
    public static final BuilderCodec<UseEntityInteraction> CODEC;

    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Client;
    }
    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
        InteractionSyncData chainData = context.getClientState();

        assert chainData != null;

        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();

        assert commandBuffer != null;

        Ref<EntityStore> targetRef = ((EntityStore)commandBuffer.getStore().getExternalData()).getRefFromNetworkId(chainData.entityId);


    }
    @Nonnull
    protected Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.UseEntityInteraction();
    }

    public boolean needsRemoteSync() {
        return true;
    }

    @Nonnull
    public String toString() {
        return "UseEntityInteraction{} " + super.toString();
    }

    static {
        CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(UseEntityInteraction.class, UseEntityInteraction::new, SimpleInstantInteraction.CODEC).documentation("Attempts to use the tame entity, executing interactions on it if any.")).build();
    }
}
