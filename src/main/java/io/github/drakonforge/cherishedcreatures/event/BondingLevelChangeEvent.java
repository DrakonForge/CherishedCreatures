package io.github.drakonforge.cherishedcreatures.event;

import com.hypixel.hytale.component.system.EcsEvent;

public class BondingLevelChangeEvent extends EcsEvent {
    private int levelChangedTo = 1;

    public BondingLevelChangeEvent(int levelChangedTo) {
        this.levelChangedTo = levelChangedTo;
    }

    public float getLevelChangedTo() {
        return this.levelChangedTo;
    }

    public void setLevelChangedTo(int levelChangedTo) {
        this.levelChangedTo = levelChangedTo;
    }
}
