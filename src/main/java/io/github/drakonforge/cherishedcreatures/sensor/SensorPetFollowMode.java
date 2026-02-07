package io.github.drakonforge.cherishedcreatures.sensor;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import io.github.drakonforge.cherishedcreatures.component.PetStateComponent;
import io.github.drakonforge.cherishedcreatures.data.PetFollowMode;
import io.github.drakonforge.cherishedcreatures.sensor.builder.BuilderSensorPetFollowMode;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SensorPetFollowMode extends SensorBase {

    protected final PetFollowMode followMode;

    public SensorPetFollowMode(@NonNullDecl BuilderSensorPetFollowMode builder, @Nonnull BuilderSupport support) {
        super(builder);
        this.followMode = builder.getPetFollowMode(support);
    }

    @Override
    public boolean matches(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Role role, double dt,
            @NonNullDecl Store<EntityStore> store) {
        if (!super.matches(ref, role, dt, store)) {
            return false;
        }
        PetStateComponent petStateComponent = store.getComponent(ref, PetStateComponent.getComponentType());
        if (petStateComponent == null) {
            return false;
        }
        return petStateComponent.getFollowMode() == followMode;
    }

    @NullableDecl
    @Override
    public InfoProvider getSensorInfo() {
        return null;
    }
}
