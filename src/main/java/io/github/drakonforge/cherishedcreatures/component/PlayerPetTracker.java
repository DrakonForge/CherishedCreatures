package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PlayerPetTracker implements Component<EntityStore> {

    public static final BuilderCodec<PlayerPetTracker> CODEC = BuilderCodec.builder(PlayerPetTracker.class, PlayerPetTracker::new).build();

    public static ComponentType<EntityStore, PlayerPetTracker> getComponentType() {
        return CherishedCreaturesPlugin.get().getPlayerPetTrackerComponentType();
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        PlayerPetTracker clone = new PlayerPetTracker();
        return clone;
    }
}
