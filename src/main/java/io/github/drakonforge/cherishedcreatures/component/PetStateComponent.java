package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import io.github.drakonforge.cherishedcreatures.data.PetFollowMode;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PetStateComponent implements Component<EntityStore> {

    public static final BuilderCodec<PetStateComponent> CODEC = BuilderCodec.builder(PetStateComponent.class, PetStateComponent::new).build();

    public static ComponentType<EntityStore, PetStateComponent> getComponentType() {
        return CherishedCreaturesPlugin.get().getPetStateComponentType();
    }

    private PetFollowMode followMode = PetFollowMode.STAY;
    private float followRadius = 0.0f;

    public PetFollowMode getFollowMode() {
        return followMode;
    }

    public float getFollowRadius() {
        return followRadius;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        PetStateComponent clone = new PetStateComponent();
        clone.followMode = followMode;
        clone.followRadius = followRadius;
        return clone;
    }
}
