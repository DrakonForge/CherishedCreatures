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
        WALK(0.15f),
        TROT(0.3f),
        CANTER(1.0f),
        GALLOP(1.35f),
        FULL_GALLOP(1.65f);

        private static final MountGait[] GAITS = {WALK, TROT, CANTER, GALLOP, FULL_GALLOP};

        public static MountGait toGait(int index) {
            if (index < 0) {
                return WALK;
            }
            if (index >= GAITS.length) {
                return FULL_GALLOP;
            }
            return GAITS[index];
        }

        private final float speedMultiplier;

        MountGait(float speedMultiplier) {
            this.speedMultiplier = speedMultiplier;
        }

        public float getSpeedMultiplier() {
            return speedMultiplier;
        }
    }

    @Nonnull
    private PetType mountedPetType;
    private MountGait desiredGait = MountGait.TROT;
    private boolean isStaminaDepleted = false;
    private long lastForwardInput = 0;
    private long lastBackwardInput = 0;
    private float heldSprintTime = 0.0f;

    // Speed
    private float lastSentSpeed = -99.0f;
    private float currentSpeed = 1.0f;
    private float currentSpeedMultiplier = 1.0f;

    public MountHandlingComponent() {
        mountedPetType = PetType.DEFAULT;
    }

    public MountHandlingComponent(@NonNullDecl PetType mountedPetType) {
        this.mountedPetType = mountedPetType;
    }

    public static ComponentType<EntityStore, MountHandlingComponent> getComponentType() {
        return CherishedCreaturesPlugin.get().getMountHandlingComponentType();
    }

    public void setDesiredGait(MountGait desiredGait, boolean instant) {
        this.desiredGait = desiredGait;
        if (instant) {
            setSpeedMultiplier(desiredGait.getSpeedMultiplier());
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

    public void setLastSentSpeed(float lastSentSpeed) {
        this.lastSentSpeed = lastSentSpeed;
    }

    public void setCurrentSpeed(float currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public void setSpeedMultiplier(float currentSpeedMultiplier) {
        this.currentSpeedMultiplier = currentSpeedMultiplier;
    }

    public boolean isStaminaDepleted() {
        return isStaminaDepleted;
    }

    @NonNullDecl
    public PetType getMountedPetType() {
        return mountedPetType;
    }

    public MountGait getDesiredGait() {
        return desiredGait;
    }

    public long getLastForwardInput() {
        return lastForwardInput;
    }

    public long getLastBackwardInput() {
        return lastBackwardInput;
    }

    public float getLastSentSpeed() {
        return lastSentSpeed;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public float getSpeedMultiplier() {
        return currentSpeedMultiplier;
    }

    public boolean isSprinting() {
        return heldSprintTime > 0.0f;
    }

    public float getHeldSprintTime() {
        return heldSprintTime;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        MountHandlingComponent clone = new MountHandlingComponent();
        clone.mountedPetType = mountedPetType;
        return clone;
    }
}
