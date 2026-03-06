package io.github.drakonforge.cherishedcreatures.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.ui.PetMenus;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class OpenPetMenuInteraction extends SimpleInstantInteraction {

    // TODO: Add a field to open pet details instead
    @Nonnull
    public static final BuilderCodec<OpenPetMenuInteraction> CODEC = BuilderCodec.builder(
                    OpenPetMenuInteraction.class, OpenPetMenuInteraction::new, SimpleInstantInteraction.CODEC)
            .documentation("Opens the pet menu.")
            .build();

    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Client;
    }

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType,
            @NonNullDecl InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();

        assert commandBuffer != null;

        World world = commandBuffer.getExternalData().getWorld();
        Store<EntityStore> store = world.getEntityStore().getStore();
        Ref<EntityStore> ref = context.getEntity();
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }
        PetMenus.openPetMenu(store, ref, playerRef);
    }

    public boolean needsRemoteSync() {
        return true;
    }

    @Nonnull
    public String toString() {
        return "TameInteraction{} " + super.toString();
    }
}
