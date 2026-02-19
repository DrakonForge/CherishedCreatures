package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import io.github.drakonforge.cherishedcreatures.data.MountStatusMeter;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class MountStatusMetersComponent implements Component<EntityStore> {

    public static ComponentType<EntityStore, MountStatusMetersComponent> getComponentType() {
        return CherishedCreaturesPlugin.get().getMountStatusMetersComponentType();
    }

    private final MountStatusMeter healthMeter;
    private final MountStatusMeter staminaMeter;

    public MountStatusMetersComponent() {
        healthMeter = new MountStatusMeter("Hud/MountHealthMeter.html");
        staminaMeter = new MountStatusMeter("Hud/MountStaminaMeter.html");
    }

    public MountStatusMetersComponent(MountStatusMeter healthMeter, MountStatusMeter staminaMeter) {
        this.healthMeter = healthMeter;
        this.staminaMeter = staminaMeter;
    }

    public MountStatusMeter getHealthMeter() {
        return healthMeter;
    }

    public MountStatusMeter getStaminaMeter() {
        return staminaMeter;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        MountStatusMetersComponent clone = new MountStatusMetersComponent(healthMeter,
                staminaMeter);
        return clone;
    }
}
