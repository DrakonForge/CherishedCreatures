package io.github.drakonforge.cherishedcreatures.stat;

import com.hypixel.hytale.builtin.mounts.NPCMountComponent;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.Condition;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.PetType.PetFeatureFlag;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import io.github.drakonforge.cherishedcreatures.component.MountedActiveComponent;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import java.time.Instant;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

// Only works for mounts w/advanced handling.
public class MountCondition extends Condition {

    public static final BuilderCodec<MountCondition> CODEC = BuilderCodec.builder(MountCondition.class, MountCondition::new, Condition.BASE_CODEC).build();

    @Override
    public boolean eval0(@NonNullDecl ComponentAccessor<EntityStore> componentAccessor,
            @NonNullDecl Ref<EntityStore> ref, @NonNullDecl Instant instant) {
        NPCMountComponent npcMountComponent = componentAccessor.getComponent(ref, NPCMountComponent.getComponentType());
        PetTypeComponent petTypeComponent = componentAccessor.getComponent(ref, PetTypeComponent.getComponentType());
        if (npcMountComponent == null || petTypeComponent == null) {
            return false;
        }
        PlayerRef playerRef = npcMountComponent.getOwnerPlayerRef();
        return playerRef != null && petTypeComponent.getPetType().hasFeatureFlag(PetFeatureFlag.AdvancedMountHandling);
    }
}
