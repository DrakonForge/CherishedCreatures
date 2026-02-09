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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PlayerPetTracker implements Component<EntityStore> {

    private static final TrackedPetEntry[] EMPTY = new TrackedPetEntry[0];

    public static final BuilderCodec<PlayerPetTracker> CODEC = BuilderCodec.builder(
                    PlayerPetTracker.class, PlayerPetTracker::new)
            .append(new KeyedCodec<>("PetEntries",
                            new ArrayCodec<>(TrackedPetEntry.CODEC, TrackedPetEntry[]::new)),
                    PlayerPetTracker::loadEntries, PlayerPetTracker::saveEntries)
            .add()
            .build();

    public static ComponentType<EntityStore, PlayerPetTracker> getComponentType() {
        return CherishedCreaturesPlugin.get().getPlayerPetTrackerComponentType();
    }
    private final List<TrackedPetEntry> petEntries = new ArrayList<>();

    private TrackedPetEntry[] saveEntries() {
        return petEntries.toArray(new TrackedPetEntry[0]);
    }

    private void loadEntries(TrackedPetEntry[] entries) {
        petEntries.clear();
        petEntries.addAll(Arrays.asList(entries));
    }

    private int findPetByUuid(UUID uuid) {
        for (int i = 0; i < petEntries.size(); ++i) {
            TrackedPetEntry petEntry = petEntries.get(i);
            if (petEntry.getUuid().equals(uuid)) {
                return i;
            }
        }
        return -1;
    }

    public TrackedPetEntry getPetEntry(int i) {
        return petEntries.get(i);
    }

    @Nullable
    public TrackedPetEntry getPetEntry(UUID uuid) {
        int index = findPetByUuid(uuid);
        if (index < 0) {
            return null;
        }
        return getPetEntry(index);
    }

    public boolean addPetEntry(TrackedPetEntry petEntry) {
        if (findPetByUuid(petEntry.getUuid()) > -1) {
            return false;
        }
        petEntries.add(petEntry);
        return true;
    }

    public boolean removePetEntry(UUID uuid) {
        boolean foundAny = false;
        for (int i = petEntries.size() - 1; i >= 0; --i) {
            if (petEntries.get(i).getUuid().equals(uuid)) {
                petEntries.remove(i);
                foundAny = true;
            }
        }
        return foundAny;
    }

    public int getNumPetEntries() {
        return petEntries.size();
    }

    public void clearPetEntries() {
        petEntries.clear();
    }

    public List<TrackedPetEntry> getPetEntries() {
        return petEntries;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        PlayerPetTracker clone = new PlayerPetTracker();
        return clone;
    }
}
