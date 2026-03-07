package io.github.drakonforge.cherishedcreatures.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;

public abstract class WhistleInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<WhistleInteraction> CODEC = BuilderCodec.abstractBuilder(
            WhistleInteraction.class, SimpleInstantInteraction.CODEC).build();
}
