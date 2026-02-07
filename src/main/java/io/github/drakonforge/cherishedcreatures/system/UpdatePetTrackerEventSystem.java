package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.event.UpdatePetTrackerEvent;

public abstract class UpdatePetTrackerEventSystem extends EntityEventSystem<EntityStore, UpdatePetTrackerEvent> {

    protected UpdatePetTrackerEventSystem() {
        super(UpdatePetTrackerEvent.class);
    }
}
