package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class MountHandlingComponent implements Component<EntityStore> {

    // Stamina drain is defined in Stamina.json
    public enum MountGait {
        Walk(0.3f),
        Trot(0.5f),
        Canter(1.0f),
        Gallop(1.25f),
        FullGallop(1.65f);

        private static final MountGait[] GAITS = { Walk, Trot, Canter, Gallop, FullGallop };

        public static MountGait toGait(int index) {
            if (index < 0) {
                return Walk;
            }
            if (index >= GAITS.length) {
                return FullGallop;
            }
            return GAITS[index];
        }

        private final float desiredSpeedMultiplier;

        MountGait(float desiredSpeedMultiplier) {
            this.desiredSpeedMultiplier = desiredSpeedMultiplier;
        }

        public float getDesiredSpeedMultiplier() {
            return desiredSpeedMultiplier;
        }
    }

    @Nonnull
    private final PetType mountedPetType;
    @Nonnull
    private final PetAttributes mountPetAttributes;
    private MountGait currentGait = MountGait.Walk;
    private boolean isStaminaDepleted = false;
    private long lastForwardInput = 0;
    private long lastBackwardInput = 0;
    private float heldSprintTime = 0.0f;

    // Speed
    private float lastSentBaseSpeed = -99.0f;
    private float lastSentSpeedMultiplier = -99.0f;
    private float baseSpeed = 1.0f;
    private float speedMultiplier = 1.0f;

    public MountHandlingComponent() {
        mountedPetType = PetType.DEFAULT;
        mountPetAttributes = PetAttributes.EMPTY;
    }

    public MountHandlingComponent(@NonNullDecl PetType mountedPetType, @NonNullDecl PetAttributes petAttributes) {
        this.mountedPetType = mountedPetType;
        this.mountPetAttributes = petAttributes;
    }

    public static ComponentType<EntityStore, MountHandlingComponent> getComponentType() {
        return CherishedCreaturesPlugin.get().getMountHandlingComponentType();
    }

    public void setCurrentGait(MountGait desiredGait, boolean instant) {
        this.currentGait = desiredGait;
        if (instant) {
            setSpeedMultiplier(desiredGait.getDesiredSpeedMultiplier());
        }
    }

    public void setStaminaDepleted(boolean staminaDepleted) {
        isStaminaDepleted = staminaDepleted;
    }

    public void setLastForwardInput(long lastForwardInput) {
        this.lastForwardInput = lastForwardInput;
    }

    public void setLastBackwardInput(long lastBackwardInput) {
        this.lastBackwardInput = lastBackwardInput;
    }

    public void incrementHeldSprintTime(float deltaTime) {
        heldSprintTime += deltaTime;
    }

    public void resetSprinting() {
        heldSprintTime = 0.0f;
    }

    public void setLastSentBaseSpeed(float lastSentBaseSpeed) {
        this.lastSentBaseSpeed = lastSentBaseSpeed;
    }

    public void setLastSentSpeedMultiplier(float lastSentSpeedMultiplier) {
        this.lastSentSpeedMultiplier = lastSentSpeedMultiplier;
    }

    public void setBaseSpeed(float baseSpeed) {
        this.baseSpeed = baseSpeed;
    }

    public void setSpeedMultiplier(float speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public boolean isStaminaDepleted() {
        return isStaminaDepleted;
    }

    @NonNullDecl
    public PetType getMountedPetType() {
        return mountedPetType;
    }

    public MountGait getCurrentGait() {
        return currentGait;
    }

    public long getLastForwardInput() {
        return lastForwardInput;
    }

    public long getLastBackwardInput() {
        return lastBackwardInput;
    }

    public float getLastSentBaseSpeed() {
        return lastSentBaseSpeed;
    }

    public float getLastSentSpeedMultiplier() {
        return lastSentSpeedMultiplier;
    }

    public float getBaseSpeed() {
        return baseSpeed;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public float getHeldSprintTime() {
        return heldSprintTime;
    }

    public PetAttributes getMountPetAttributes() {
        return mountPetAttributes;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        MountHandlingComponent clone = new MountHandlingComponent(mountedPetType, mountPetAttributes);
        return clone;
    }
}
