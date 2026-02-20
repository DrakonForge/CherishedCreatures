package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent.MountGait;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

// Here we store all info the mount wants to cache
public class MountHandlingNpcComponent implements Component<EntityStore> {

    // Provide a ref to the current gait without us needing to look up the rider's MountHandlingComponent
    private MountGait currentGait = MountGait.Walk;
    // Mount velocity is NaN so provide the rider's velocity
    private Vector3d velocity = Vector3d.ZERO;

    public static ComponentType<EntityStore, MountHandlingNpcComponent> getComponentType() {
        return CherishedCreaturesPlugin.get().getMountHandlingNpcComponentType();
    }

    public void setCurrentGait(MountGait desiredGait) {
        this.currentGait = desiredGait;
    }

    public void setVelocity(Vector3d velocity) {
        this.velocity = velocity;
    }

    public MountGait getCurrentGait() {
        return currentGait;
    }

    public Vector3d getVelocity() {
        return velocity;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        MountHandlingNpcComponent clone = new MountHandlingNpcComponent();
        clone.currentGait = currentGait;
        return clone;
    }
}
