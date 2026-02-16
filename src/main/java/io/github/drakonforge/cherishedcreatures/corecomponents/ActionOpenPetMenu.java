package io.github.drakonforge.cherishedcreatures.corecomponents;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.corecomponents.builder.BuilderActionOpenPetMenu;
import io.github.drakonforge.cherishedcreatures.ui.PetMenus;
import java.util.UUID;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ActionOpenPetMenu extends ActionBase {

    public ActionOpenPetMenu(@NonNullDecl BuilderActionOpenPetMenu builder) {
        super(builder);
    }

    @Override
    public boolean execute(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Role role,
            InfoProvider sensorInfo, double dt, @NonNullDecl Store<EntityStore> store) {
        super.execute(ref, role, sensorInfo, dt, store);
        PetComponent petComponent = store.getComponent(ref, PetComponent.getComponentType());
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
        if (petComponent == null || uuidComponent == null) {
            return false;
        }
        Ref<EntityStore> playerRef = role.getStateSupport().getInteractionIterationTarget();
        if (playerRef == null || !playerRef.isValid()) {
            return false;
        }
        PlayerRef playerRefComponent = store.getComponent(playerRef, PlayerRef.getComponentType());
        assert playerRefComponent != null;
        UUID petUuid = uuidComponent.getUuid();

        return PetMenus.openPetDetails(store, playerRef, playerRefComponent, petUuid);
    }
}
