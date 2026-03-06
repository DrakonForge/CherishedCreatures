package io.github.drakonforge.cherishedcreatures.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.util.PetHelpers;
import io.github.drakonforge.cherishedcreatures.util.PetHelpers.TameResult;
import java.awt.Color;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jetbrains.annotations.Nullable;

public class TameInteraction extends SimpleInstantInteraction {

    @Nonnull
    public static final BuilderCodec<TameInteraction> CODEC = BuilderCodec.builder(
                    TameInteraction.class, TameInteraction::new, SimpleInstantInteraction.CODEC)
            .documentation("Attempts to bond with the target entity.")
            .build();

    @Nullable
    private static String getErrorMessageForResult(TameResult result) {
        if (result == TameResult.FAIL_NOT_TAMEABLE) {
            return "cherishedcreatures.interactions.tame.invalidTarget";
        } else if (result == TameResult.FAIL_ALREADY_TAMED_BY_SELF) {
            return "cherishedcreatures.interactions.tame.alreadyTamedBySelf";
        } else if (result == TameResult.FAIL_ALREADY_TAMED_BY_OTHERS) {
            return "cherishedcreatures.interactions.tame.alreadyTamedByOthers";
        }
        return null;
    }

    public static Message getPetDisplayName(@NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl Ref<EntityStore> targetEntity) {
        DisplayNameComponent displayNameComponent = commandBuffer.getComponent(targetEntity,
                DisplayNameComponent.getComponentType());
        if (displayNameComponent != null) {
            return displayNameComponent.getDisplayName();
        }
        return Message.translation("cherishedcreatures.pet.defaultName");
    }

    private static void sendMessage(@NonNullDecl InteractionContext context,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Message message) {
        Ref<EntityStore> ref = context.getOwningEntity();
        PlayerRef playerRef = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef != null) {
            playerRef.sendMessage(message);
        }
    }

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

        if (targetEntity == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }

        TameResult result = PetHelpers.attemptTame(store, ref, targetEntity);
        if (result != TameResult.SUCCESS) {
            context.getState().state = InteractionState.Failed;
            // Only send error messages when the failure state would be unclear to the player
            String errorMessage = getErrorMessageForResult(result);
            if (errorMessage != null) {
                sendMessage(context, commandBuffer,
                        Message.translation(errorMessage).color(Color.YELLOW));
            }
            return;
        }

        Message petName = getPetDisplayName(commandBuffer, targetEntity);
        sendMessage(context, commandBuffer,
                Message.translation("cherishedcreatures.interactions.tame.success")
                        .param("name", petName)
                        .color(Color.GREEN));
    }

    public boolean needsRemoteSync() {
        return true;
    }

    @Nonnull
    public String toString() {
        return "TameInteraction{} " + super.toString();
    }
}
