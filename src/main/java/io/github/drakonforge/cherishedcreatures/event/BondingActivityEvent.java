package io.github.drakonforge.cherishedcreatures.event;

import com.hypixel.hytale.component.system.EcsEvent;
import io.github.drakonforge.cherishedcreatures.data.BondingActivityType;

public class BondingActivityEvent extends EcsEvent {
    private final BondingActivityType type;

    public BondingActivityEvent(BondingActivityType type) {
        this.type = type;
    }

    public BondingActivityType getType() {
        return type;
    }
}
