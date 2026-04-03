package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PlayerUIPreferencesComponent implements Component<EntityStore> {
    public static final BuilderCodec<PlayerUIPreferencesComponent> CODEC = BuilderCodec.builder(
                    PlayerUIPreferencesComponent.class, PlayerUIPreferencesComponent::new)
            .append(new KeyedCodec<>("PetListLayoutPreference", Codec.INTEGER, true),
                    (data, preference) -> data.petListLayoutPreference = preference, PlayerUIPreferencesComponent::getPetListLayoutPreference)
            .add()
            .build();

    public int petListLayoutPreference = 1;
    public static int PetLayoutList = 0;
    public static int PetLayoutGrid = 1;
    public static ComponentType<EntityStore, PlayerUIPreferencesComponent> getComponentType() {
        return CherishedCreaturesPlugin.get().getPlayerUIPreferencesComponent();
    }
    public int getPetListLayoutPreference() {
        return this.petListLayoutPreference;
    }

    public void toggleLayoutPreference() {
        if (this.petListLayoutPreference == 0) {
            this.petListLayoutPreference = 1;
        } else {
            this.petListLayoutPreference = 0;
        }
    }
    @NullableDecl

    @java.lang.Override
    public Component<EntityStore> clone() {
        PlayerUIPreferencesComponent clone = new PlayerUIPreferencesComponent();
        return clone;
    }


}
