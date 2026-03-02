package io.github.drakonforge.cherishedcreatures.event;

import com.hypixel.hytale.component.system.EcsEvent;

public class BondingXpEvent extends EcsEvent {
    private float amountGained;

    public BondingXpEvent(float amountGained) {
        this.amountGained = amountGained;
    }

    public float getAmountGained() {
        return amountGained;
    }

    public void setAmountGained(float amountGained) {
        this.amountGained = amountGained;
    }
}
