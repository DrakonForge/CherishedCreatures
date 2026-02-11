package io.github.drakonforge.cherishedcreatures.system;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class RegisterDefaultPetTypeSystem extends HolderSystem<EntityStore> {
    private static final Map<String, String> idToPetTypeId = createDefaultIdToPetTypeIdMap();

    // TODO: Hardcoding for now
    private static Map<String, String> createDefaultIdToPetTypeIdMap() {
        Map<String, String> idToPetTypeId = new HashMap<>();
        idToPetTypeId.put("Mouse", "DrakonForge_Generic");
        idToPetTypeId.put("Cat", "DrakonForge_Generic");
        idToPetTypeId.put("Cow", "DrakonForge_Generic");
        idToPetTypeId.put("Crab", "DrakonForge_Generic");
        idToPetTypeId.put("Rabbit", "DrakonForge_Generic");
        return idToPetTypeId;
    }

    @Override
    public void onEntityAdd(@NonNullDecl Holder<EntityStore> holder,
            @NonNullDecl AddReason addReason, @NonNullDecl Store<EntityStore> store) {
        NPCEntity npcComponent = holder.getComponent(
                Objects.requireNonNull(NPCEntity.getComponentType()));
        assert npcComponent != null;

        if (holder.getComponent(PetTypeComponent.getComponentType()) != null) {
            return;
        }

        String id = npcComponent.getNPCTypeId();
        String petTypeId = idToPetTypeId.get(id);
        if (petTypeId == null) {
            petTypeId = "DrakonForge_Generic"; // TODO: Temporary debug, let anything be tamed
        }
        if (petTypeId != null) {
            holder.addComponent(PetTypeComponent.getComponentType(), new PetTypeComponent(petTypeId));
        }
    }

    @Override
    public void onEntityRemoved(@NonNullDecl Holder<EntityStore> holder,
            @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store) {

    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return NPCEntity.getComponentType();
    }
}
