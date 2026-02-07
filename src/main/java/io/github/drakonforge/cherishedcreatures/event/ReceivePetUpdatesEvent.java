package io.github.drakonforge.cherishedcreatures.event;

import com.hypixel.hytale.component.system.EcsEvent;
import io.github.drakonforge.cherishedcreatures.update.PetUpdate;
import java.util.List;

public class ReceivePetUpdatesEvent extends EcsEvent {
    private final List<PetUpdate> petUpdates;
    private final boolean isImmediate;


    public ReceivePetUpdatesEvent(List<PetUpdate> petUpdates, boolean isImmediate) {
        this.petUpdates = petUpdates;
        this.isImmediate = isImmediate;
    }

    public List<PetUpdate> getPetUpdates() {
        return petUpdates;
    }

    public boolean isImmediate() {
        return isImmediate;
    }
}
