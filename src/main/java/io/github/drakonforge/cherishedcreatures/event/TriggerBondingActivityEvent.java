package io.github.drakonforge.cherishedcreatures.event;

import com.hypixel.hytale.component.system.EcsEvent;
import io.github.drakonforge.cherishedcreatures.data.BondingActivityType;

public class TriggerBondingActivityEvent extends EcsEvent {
    private final BondingActivityType type;
    private final boolean isForced;

    public TriggerBondingActivityEvent(BondingActivityType type) {
        this(type, false);
    }

    public TriggerBondingActivityEvent(BondingActivityType type, boolean isForced) {
        this.type = type;
        this.isForced = isForced;
    }

    public BondingActivityType getType() {
        return type;
    }

    public boolean isForced() {
        return isForced;
    }
}
