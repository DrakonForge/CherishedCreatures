package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PlayerPetTracker implements Component<EntityStore> {

    private static final TrackedPetEntry[] EMPTY = new TrackedPetEntry[0];

    public static final BuilderCodec<PlayerPetTracker> CODEC = BuilderCodec.builder(
                    PlayerPetTracker.class, PlayerPetTracker::new)
            .append(new KeyedCodec<>("PetEntries",
                            new ArrayCodec<>(TrackedPetEntry.CODEC, TrackedPetEntry[]::new)),
                    (data, petEntries) -> data.petEntries = petEntries,
                    data -> data.petEntries)
            .add()
            .build();

    public static ComponentType<EntityStore, PlayerPetTracker> getComponentType() {
        return CherishedCreaturesPlugin.get().getPlayerPetTrackerComponentType();
    }
    private final List<TrackedPetEntry> petEntryUpdates = new ArrayList<>(); // TODO: Update system
    private TrackedPetEntry[] petEntries = EMPTY;

    public void resolveChanges() {
        // TODO: Right now, it's only adding entries, but we may want to update/remove them later
        if (petEntryUpdates.isEmpty()) {
            return;
        }

        // Update existing entries
        for (int i = petEntryUpdates.size() - 1; i > 0; --i) {
            TrackedPetEntry newPetEntry = petEntryUpdates.get(i);
            int petIndex = findPetByUuid(newPetEntry.getUuid());
            if (petIndex > -1) {
                petEntries[petIndex] = newPetEntry;
                petEntryUpdates.remove(i);
            }
        }

        if (!petEntryUpdates.isEmpty()) {
            TrackedPetEntry[] newPetEntries = new TrackedPetEntry[petEntries.length + petEntryUpdates.size()];
            System.arraycopy(petEntries, 0, newPetEntries, 0, petEntries.length);
            for (int i = 0; i < petEntryUpdates.size(); ++i) {
                newPetEntries[petEntries.length + i] = petEntryUpdates.get(i);
            }
            petEntries = newPetEntries;
        }

        petEntryUpdates.clear();
    }

    private int findPetByUuid(UUID uuid) {
        for (int i = 0; i < petEntries.length; ++i) {
            TrackedPetEntry petEntry = petEntries[i];
            if (petEntry.getUuid().equals(uuid)) {
                return i;
            }
        }
        return -1;
    }

    public List<TrackedPetEntry> getPetEntryUpdates() {
        return petEntryUpdates;
    }

    public TrackedPetEntry getPetEntry(int i) {
        return petEntries[i];
    }

    public void addPetEntry(TrackedPetEntry petEntry) {
        petEntryUpdates.add(petEntry);
    }

    public int getNumPetEntries() {
        return petEntries.length;
    }

    public void clearPetEntries() {
        petEntries = EMPTY;
        petEntryUpdates.clear();
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        PlayerPetTracker clone = new PlayerPetTracker();
        return clone;
    }
}
