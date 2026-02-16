package io.github.drakonforge.cherishedcreatures.event;

import com.hypixel.hytale.component.system.EcsEvent;
import io.github.drakonforge.cherishedcreatures.data.PetActivityType;

// TODO: We may want to rename this to just "PetActivity" and let it trigger for any pet
// Just in case a pet still uses Happiness but not Bonding
public class TriggerPetActivityEvent extends EcsEvent {
    private final PetActivityType type;
    private final boolean isForced;

    public TriggerPetActivityEvent(PetActivityType type) {
        this(type, false);
    }

    public TriggerPetActivityEvent(PetActivityType type, boolean isForced) {
        this.type = type;
        this.isForced = isForced;
    }

    public PetActivityType getType() {
        return type;
    }

    public boolean isForced() {
        return isForced;
    }
}
