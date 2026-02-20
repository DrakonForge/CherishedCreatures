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

    public enum MountGait {
        WALK(0.3f),
        TROT(0.3f),
        CANTER(1.0f),
        GALLOP(1.65f);

        MountGait(float speedMultiplier) {

        }
    }

    @Nonnull
    private PetType mountedPetType;
    private MountGait desiredGait = MountGait.TROT;
    private boolean isStaminaDepleted = false;
    private long lastForwardInput = 0;
    private long lastBackwardInput = 0;

    public MountHandlingComponent() {
        mountedPetType = PetType.DEFAULT;
    }

    public MountHandlingComponent(@NonNullDecl PetType mountedPetType) {
        this.mountedPetType = mountedPetType;
    }

    public static ComponentType<EntityStore, MountHandlingComponent> getComponentType() {
        return CherishedCreaturesPlugin.get().getMountHandlingComponentType();
    }

    public void setDesiredGait(MountGait desiredGait) {
        this.desiredGait = desiredGait;
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

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        MountHandlingComponent clone = new MountHandlingComponent();
        clone.mountedPetType = mountedPetType;
        return clone;
    }
}
