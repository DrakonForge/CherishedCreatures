package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PetBondComponent implements Component<EntityStore> {

    public static final BuilderCodec<PetBondComponent> CODEC = BuilderCodec.builder(
                    PetBondComponent.class, PetBondComponent::new).build();

    public static ComponentType<EntityStore, PetBondComponent> getComponentType() {
        return CherishedCreaturesPlugin.get().getPetBondComponentType();
    }

    private int bondingLevel;
    private float bondingXp;

    public void addBondingXp(float baseAmount) {
        // TODO: Bonding XP multipliers
        bondingXp += baseAmount;
        recalculateBondingLevel();
    }

    public void recalculateBondingLevel() {
        // TODO: If level changed, fire an event
    }

    public int getBondingLevel() {
        return bondingLevel;
    }

    public float getBondingXp() {
        return bondingXp;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        PetBondComponent clone = new PetBondComponent();
        clone.bondingLevel = bondingLevel;
        clone.bondingXp = bondingXp;
        return clone;
    }
}
