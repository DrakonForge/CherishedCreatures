package io.github.drakonforge.cherishedcreatures.stat;

import com.hypixel.hytale.builtin.mounts.NPCMountComponent;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.Condition;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.PetType.PetFeatureFlag;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent.MountGait;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import java.time.Instant;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

// Only works for mounts w/advanced handling.
public class MountCondition extends Condition {

    public static final BuilderCodec<MountCondition> CODEC = BuilderCodec.builder(
                    MountCondition.class, MountCondition::new, Condition.BASE_CODEC)
            .append(new KeyedCodec<>("MustBeMoving", Codec.BOOLEAN),
                    (condition, value) -> condition.mustBeMoving = value,
                    condition -> condition.mustBeMoving)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("Gait", new EnumCodec<>(MountGait.class)),
                    (condition, value) -> condition.gait = value, condition -> condition.gait)
            .documentation("TODO")
            .add()
            .documentation("TODO")
            .build();
    protected boolean mustBeMoving = true;
    @Nullable
    protected MountGait gait = null;

    @Override
    public boolean eval0(@NonNullDecl ComponentAccessor<EntityStore> componentAccessor,
            @NonNullDecl Ref<EntityStore> ref, @NonNullDecl Instant instant) {
        NPCMountComponent npcMountComponent = componentAccessor.getComponent(ref,
                NPCMountComponent.getComponentType());
        PetTypeComponent petTypeComponent = componentAccessor.getComponent(ref,
                PetTypeComponent.getComponentType());
        if (npcMountComponent == null || petTypeComponent == null){
            return false;
        }
        if (!petTypeComponent.getPetType().hasFeatureFlag(PetFeatureFlag.AdvancedMountHandling)) {
            return false;
        }
        PlayerRef playerRef = npcMountComponent.getOwnerPlayerRef();
        if (playerRef == null) {
            return false;
        }

        if (gait != null) {
            Ref<EntityStore> riderRef = playerRef.getReference();
            if (riderRef == null || !riderRef.isValid()) {
                return false;
            }
            MountHandlingComponent mountHandlingComponent = componentAccessor.getComponent(riderRef, MountHandlingComponent.getComponentType());
            if (mountHandlingComponent == null) {
                return false;
            }
            if (mountHandlingComponent.getCurrentGait() != gait) {
                return false;
            }
        }

        MovementStatesComponent movementStatesComponent = componentAccessor.getComponent(ref,
                MovementStatesComponent.getComponentType());
        if (movementStatesComponent == null) {
            return false;
        }
        return !mustBeMoving || !movementStatesComponent.getMovementStates().idle;
    }
}
