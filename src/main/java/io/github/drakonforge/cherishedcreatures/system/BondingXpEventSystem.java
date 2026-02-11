package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.event.BondingXpEvent;

public abstract class BondingXpEventSystem extends EntityEventSystem<EntityStore, BondingXpEvent> {

    public BondingXpEventSystem() {
        super(BondingXpEvent.class);
    }
}
