package io.github.drakonforge.cherishedcreatures.sensor;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import io.github.drakonforge.cherishedcreatures.component.PetBondComponent;
import io.github.drakonforge.cherishedcreatures.sensor.builder.BuilderSensorBondingLevel;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SensorBondingLevel extends SensorBase {

    private final int minLevel;
    private final int maxLevel;

    public SensorBondingLevel(@NonNullDecl BuilderSensorBondingLevel builder, @NonNullDecl BuilderSupport builderSupport) {
        super(builder);
        int[] levelRange = builder.getLevelRange(builderSupport);
        minLevel = levelRange[0];
        maxLevel = levelRange[1];
    }

    @NullableDecl
    @Override
    public InfoProvider getSensorInfo() {
        return null;
    }

    @Override
    public boolean matches(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Role role, double dt,
            @NonNullDecl Store<EntityStore> store) {
        if(!super.matches(ref, role, dt, store)) {
            return false;
        }
        PetBondComponent petBondComponent = store.getComponent(ref, PetBondComponent.getComponentType());
        if (petBondComponent == null) {
            return false;
        }
        int bondingLevel = petBondComponent.getBondingLevel();
        return minLevel <= bondingLevel && bondingLevel <= maxLevel;
    }
}
