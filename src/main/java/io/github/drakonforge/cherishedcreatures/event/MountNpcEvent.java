package io.github.drakonforge.cherishedcreatures.event;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.system.EcsEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class MountNpcEvent extends EcsEvent {
    private final Ref<EntityStore> newMountRef;

    public MountNpcEvent(Ref<EntityStore> newMountRef) {
        this.newMountRef = newMountRef;
    }

    public Ref<EntityStore> getNewMountRef() {
        return newMountRef;
    }
}
