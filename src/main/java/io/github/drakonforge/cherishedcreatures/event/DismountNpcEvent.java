package io.github.drakonforge.cherishedcreatures.event;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.system.EcsEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class DismountNpcEvent extends EcsEvent {
    private final Ref<EntityStore> oldMountRef;

    public DismountNpcEvent(Ref<EntityStore> oldMountRef) {
        this.oldMountRef = oldMountRef;
    }

    public Ref<EntityStore> getOldMountRef() {
        return oldMountRef;
    }
}
