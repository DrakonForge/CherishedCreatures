package io.github.drakonforge.cherishedcreatures.corecomponents;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.EntityPositionProvider;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.corecomponents.builder.BuilderSensorPetOwner;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

// Looks for an active pet owner in the same world
public class SensorPetOwner extends SensorBase {
    protected final EntityPositionProvider positionProvider = new EntityPositionProvider();

    public SensorPetOwner(@Nonnull BuilderSensorPetOwner builder) {
        super(builder);
    }

    public boolean matches(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, double deltaTime, @Nonnull Store<EntityStore> store) {
        if (!super.matches(ref, role, deltaTime, store)) {
            this.positionProvider.clear();
            return false;
        }
        PetComponent petComponent = store.getComponent(ref, PetComponent.getComponentType());
        if (petComponent == null) {
            this.positionProvider.clear();
            return false;
        }

        UUID ownerUuid = petComponent.getOwnerUuid();
        if (ownerUuid == null) {
            this.positionProvider.clear();
            return false;
        }

        PlayerRef activePlayerRef = Universe.get().getPlayer(ownerUuid);
        if (activePlayerRef == null || !activePlayerRef.isValid() || !store.getExternalData().getWorld().getWorldConfig().getUuid().equals(activePlayerRef.getWorldUuid())) {
            this.positionProvider.clear();
            return false;
        }

        return positionProvider.setTarget(activePlayerRef.getReference(), store) != null;
    }

    @NullableDecl
    @Override
    public InfoProvider getSensorInfo() {
        return this.positionProvider;
    }
}
