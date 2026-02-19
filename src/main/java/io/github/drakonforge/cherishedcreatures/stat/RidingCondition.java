package io.github.drakonforge.cherishedcreatures.stat;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.Condition;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import io.github.drakonforge.cherishedcreatures.component.MountedActiveComponent;
import java.time.Instant;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

// Only works for mounts w/advanced handling.
public class RidingCondition extends Condition {

    public static final BuilderCodec<RidingCondition> CODEC = BuilderCodec.builder(RidingCondition.class, RidingCondition::new, Condition.BASE_CODEC).build();

    @Override
    public boolean eval0(@NonNullDecl ComponentAccessor<EntityStore> componentAccessor,
            @NonNullDecl Ref<EntityStore> ref, @NonNullDecl Instant instant) {
        MountedActiveComponent mountedActiveComponent = componentAccessor.getComponent(ref, MountedActiveComponent.getComponentType());
        MountHandlingComponent mountHandlingComponent = componentAccessor.getComponent(ref, MountHandlingComponent.getComponentType());
        return mountedActiveComponent != null && mountHandlingComponent != null;
    }
}
