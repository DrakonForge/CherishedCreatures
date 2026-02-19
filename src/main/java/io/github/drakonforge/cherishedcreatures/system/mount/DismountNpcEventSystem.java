package io.github.drakonforge.cherishedcreatures.system.mount;

import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.event.DismountNpcEvent;
import io.github.drakonforge.cherishedcreatures.event.MountNpcEvent;

public abstract class DismountNpcEventSystem extends EntityEventSystem<EntityStore, DismountNpcEvent> {

    public DismountNpcEventSystem() {
        super(DismountNpcEvent.class);
    }
}
