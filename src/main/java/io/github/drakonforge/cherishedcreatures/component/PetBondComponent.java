package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.Object2FloatMapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import io.github.drakonforge.cherishedcreatures.asset.BondingActivity;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectFloatMutablePair;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PetBondComponent implements Component<EntityStore> {

    public static final BuilderCodec<PetBondComponent> CODEC = BuilderCodec.builder(
                    PetBondComponent.class, PetBondComponent::new)
            .append(new KeyedCodec<>("BondingLevel", Codec.INTEGER, true),
                    (data, level) -> data.bondingLevel = level, PetBondComponent::getBondingLevel)
            .add()
            .append(new KeyedCodec<>("BondingXp", Codec.FLOAT, true),
                    (data, xp) -> data.bondingXp = xp, PetBondComponent::getBondingXp)
            .add()
            .append(new KeyedCodec<>("ActivityCooldowns",
                            new Object2FloatMapCodec<>(Codec.STRING, Object2FloatOpenHashMap::new)),
                    PetBondComponent::loadActivityCooldowns,
                    PetBondComponent::saveActivityCooldowns)
            .add()
            .build();
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static ComponentType<EntityStore, PetBondComponent> getComponentType() {
        return CherishedCreaturesPlugin.get().getPetBondComponentType();
    }
    List<ObjectFloatMutablePair<String>> activityCooldowns = new ArrayList<>();
    private int bondingLevel = 0;
    private float bondingXp = 0.0f;

    private Object2FloatMap<String> saveActivityCooldowns() {
        Object2FloatMap<String> map = new Object2FloatOpenHashMap<>();
        for (ObjectFloatMutablePair<String> pair : activityCooldowns) {
            map.put(pair.key(), pair.valueFloat());
        }
        return map;
    }

    private void loadActivityCooldowns(Object2FloatMap<String> map) {
        activityCooldowns.clear();
        for (Entry<String> entry : map.object2FloatEntrySet()) {
            addActivityCooldown(entry.getKey(), entry.getFloatValue());
        }
    }

    public void addBondingXp(float baseAmount) {
        // TODO: Bonding XP multipliers
        LOGGER.atInfo().log("Gained " + baseAmount + " bonding XP");
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

    public void triggerActivity(String activityName) {
        triggerActivity(activityName, false);
    }

    public void triggerActivity(String activityName, boolean force) {
        BondingActivity activity = BondingActivity.getAssetStore()
                .getAssetMap()
                .getAsset(activityName);
        if (activity == null) {
            LOGGER.atWarning().log("Unknown bonding activity " + activityName);
            return;
        }
        int index = getIndexForActivity(activityName);
        if (index > -1) {
            if (!force) {
                // On cooldown, skip adding bonding XP
                return;
            }
            activityCooldowns.get(index).value(activity.getCooldown());
        } else {
            addActivityCooldown(activityName, activity.getCooldown());
        }
        addBondingXp(activity.getBaseXp());
    }

    private int getIndexForActivity(String activityName) {
        for (int i = 0; i < activityCooldowns.size(); ++i) {
            if (activityCooldowns.get(i).key().equals(activityName)) {
                return i;
            }
        }
        return -1;
    }

    private void addActivityCooldown(String activityName, float value) {
        this.activityCooldowns.add(new ObjectFloatMutablePair<>(activityName, value));
    }

    public void tickActivityCooldowns(float deltaTime) {
        for (int i = activityCooldowns.size() - 1; i >= 0; --i) {
            ObjectFloatMutablePair<String> pair = activityCooldowns.get(i);
            float newValue = pair.valueFloat() - deltaTime;
            if (newValue <= 0) {
                activityCooldowns.remove(i);
            } else {
                pair.value(newValue);
            }
        }
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        PetBondComponent clone = new PetBondComponent();
        clone.bondingLevel = bondingLevel;
        clone.bondingXp = bondingXp;
        clone.activityCooldowns = activityCooldowns;
        return clone;
    }
}
