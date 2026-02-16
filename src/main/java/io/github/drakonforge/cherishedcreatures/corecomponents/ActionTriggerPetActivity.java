package io.github.drakonforge.cherishedcreatures.corecomponents;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.corecomponents.builder.BuilderActionTriggerPetActivity;
import io.github.drakonforge.cherishedcreatures.data.PetActivityType;
import io.github.drakonforge.cherishedcreatures.event.TriggerPetActivityEvent;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ActionTriggerPetActivity extends ActionBase {
    protected final PetActivityType petActivityType;

    public ActionTriggerPetActivity(@Nonnull BuilderActionTriggerPetActivity builder, @Nonnull
            BuilderSupport builderSupport) {
        super(builder);
        this.petActivityType = builder.getBondingActivityType(builderSupport);
    }

    @Override
    public boolean execute(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Role role,
            InfoProvider sensorInfo, double dt, @NonNullDecl Store<EntityStore> store) {
        super.execute(ref, role, sensorInfo, dt, store);
        PetComponent petComponent = store.getComponent(ref, PetComponent.getComponentType());
        if (petComponent == null) {
            return false;
        }
        store.invoke(ref, new TriggerPetActivityEvent(petActivityType));
        return true;
    }
}
