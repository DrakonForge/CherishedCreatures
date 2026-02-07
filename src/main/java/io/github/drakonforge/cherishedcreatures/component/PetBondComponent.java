package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.Object2FloatMapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PetBondComponent implements Component<EntityStore> {

    public static final BuilderCodec<PetBondComponent> CODEC = BuilderCodec.builder(
                    PetBondComponent.class, PetBondComponent::new)
                .append(new KeyedCodec<>("BondingLevel", Codec.INTEGER, true),
                    (data,level) -> data.bondingLevel = level,
                    PetBondComponent::getBondingLevel)
                .add()
                .append(new KeyedCodec<>("BondingXp", Codec.FLOAT, true),
                    (data,xp) -> data.bondingXp = xp,
                    PetBondComponent::getBondingXp)
                .add()
                .append(new KeyedCodec<>("ActivityCooldowns", new Object2FloatMapCodec<>(Codec.STRING, Object2FloatOpenHashMap::new)),
                    (data,value) -> data.activityCooldowns = value,
                    (data) -> data.activityCooldowns)
                .add()
                .build();

    public static ComponentType<EntityStore, PetBondComponent> getComponentType() {
        return CherishedCreaturesPlugin.get().getPetBondComponentType();
    }

    private int bondingLevel;
    private float bondingXp;

    
    private Object2FloatMap<String> activityCooldowns = new Object2FloatOpenHashMap<>();

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

    public float getActivityCooldown(String activityName) {
        return this.activityCooldowns.getOrDefault(activityName, -1.0f);
    }

    public void setActivityCooldown(String activityName, float cooldown) {
        this.activityCooldowns.put(activityName, cooldown);
    }

    public Object2FloatMap<String> getActivityCooldowns() {
        return activityCooldowns;
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
