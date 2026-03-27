package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import io.github.drakonforge.cherishedcreatures.data.PetFollowMode;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PetStateComponent implements Component<EntityStore> {

    public static final BuilderCodec<PetStateComponent> CODEC = BuilderCodec.builder(
                    PetStateComponent.class, PetStateComponent::new)
            .append(new KeyedCodec<>("FollowMode", new EnumCodec<>(PetFollowMode.class)),
                    PetStateComponent::setFollowMode, PetStateComponent::getFollowMode)
            .add()
            // TODO: Validators
            .append(new KeyedCodec<>("StayRoamingRadius", Codec.FLOAT),
                    PetStateComponent::setStayRoamingRadius,
                    PetStateComponent::getStayRoamingRadius)
            .add()
            .append(new KeyedCodec<>("FollowRoamingRadius", Codec.FLOAT),
                    PetStateComponent::setFollowRoamingRadius,
                    PetStateComponent::getFollowRoamingRadius)
            .add()
            .append(new KeyedCodec<>("HuntingEnabled", Codec.BOOLEAN),
                    PetStateComponent::setHuntingEnabled, PetStateComponent::isHuntingEnabled)
            .add()
            .build();

    public static ComponentType<EntityStore, PetStateComponent> getComponentType() {
        return CherishedCreaturesPlugin.get().getPetStateComponentType();
    }

    private PetFollowMode followMode = PetFollowMode.Stay;
    private float stayRoamingRadius = 0.0f;
    private float followRoamingRadius = 10.0f;
    private boolean huntingEnabled = false;

    public void setFollowMode(PetFollowMode followMode) {
        this.followMode = followMode;
    }

    // TODO: Bounds checks
    public void setStayRoamingRadius(float stayRoamingRadius) {
        this.stayRoamingRadius = stayRoamingRadius;
    }

    // TODO: Bounds checks
    public void setFollowRoamingRadius(float followRoamingRadius) {
        this.followRoamingRadius = followRoamingRadius;
    }

    public void setHuntingEnabled(boolean huntingEnabled) {
        this.huntingEnabled = huntingEnabled;
    }

    public PetFollowMode getFollowMode() {
        return followMode;
    }

    public float getStayRoamingRadius() {
        return stayRoamingRadius;
    }

    public float getFollowRoamingRadius() {
        return followRoamingRadius;
    }

    public boolean isHuntingEnabled() {
        return huntingEnabled;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        PetStateComponent clone = new PetStateComponent();
        clone.followMode = followMode;
        clone.stayRoamingRadius = stayRoamingRadius;
        clone.followRoamingRadius = followRoamingRadius;
        clone.huntingEnabled = huntingEnabled;
        return clone;
    }
}
