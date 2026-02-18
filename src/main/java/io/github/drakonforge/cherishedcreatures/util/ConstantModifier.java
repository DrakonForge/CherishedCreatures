package io.github.drakonforge.cherishedcreatures.util;

import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

// Alternative for StaticModifier, locks the stat to a certain value
// Will be flimsy since modifier order is not guaranteed.
public class ConstantModifier extends StaticModifier {
    // Should be no need for codec, since this should not save.
    // public static final BuilderCodec<ConstantModifier> CODEC;
    // public static final BuilderCodec<ConstantModifier> ENTITY_CODEC;

    @Override
    public float apply(float statValue) {
        return getAmount();
    }

    public ConstantModifier(Modifier.ModifierTarget target, float amount) {
        super(target, CalculationType.ADDITIVE, amount);
    }
}

