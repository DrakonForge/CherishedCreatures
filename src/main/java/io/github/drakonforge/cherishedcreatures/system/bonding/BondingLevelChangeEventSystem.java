package io.github.drakonforge.cherishedcreatures.system.bonding;

import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.event.BondingLevelChangeEvent;

public abstract class BondingLevelChangeEventSystem extends EntityEventSystem<EntityStore, BondingLevelChangeEvent> {

    public BondingLevelChangeEventSystem() {
        super(BondingLevelChangeEvent.class);
    }
}
