package io.github.drakonforge.cherishedcreatures.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.util.PetHelpers;
import org.jetbrains.annotations.NotNull;

public class LongWhistleInteraction extends WhistleInteraction {
    public static final BuilderCodec<LongWhistleInteraction> CODEC = BuilderCodec.builder(
            LongWhistleInteraction.class, LongWhistleInteraction::new, WhistleInteraction.CODEC).build();

    @Override
    protected void firstRun(@NotNull InteractionType interactionType,
            @NotNull InteractionContext interactionContext,
            @NotNull CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        assert commandBuffer != null;

        Ref<EntityStore> ref = interactionContext.getEntity();
        PetHelpers.doLongWhistle(commandBuffer, ref);
    }
}
