package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PlayerNpcMountDetection implements Component<EntityStore> {

    public static final float STAMINA_REGEN_RATE = 2.0f; // In points per second -- slightly slower than normal stamina regeneration
    public static final String MOUNT_MAX_STAMINA_MODIFIER = "MountMaxStamina";

    @Nullable
    private Ref<EntityStore> currentMount = null;
    private float storedStaminaValue = 0.0f;
    private float maxStoredStaminaValue = 10.0f;

    public static ComponentType<EntityStore, PlayerNpcMountDetection> getComponentType() {
        return CherishedCreaturesPlugin.get().getPlayerNpcMountDetectionComponentType();
    }

    public void setCurrentMount(@Nullable Ref<EntityStore> currentMount) {
        this.currentMount = currentMount;
    }

    public void addStoredStamina(float value) {
        if (value <= 0) {
            return;
        }
        this.storedStaminaValue = Math.min(this.storedStaminaValue + value, maxStoredStaminaValue);
    }

    public void setLastPlayerStaminaValue(float lastPlayerStaminaValue, float maxValue) {
        this.storedStaminaValue = lastPlayerStaminaValue;
        this.maxStoredStaminaValue = maxValue;
    }

    @Nullable
    public Ref<EntityStore> getCurrentMount() {
        return currentMount;
    }

    public float getStoredStaminaValue() {
        return storedStaminaValue;
    }

    public boolean shouldRegenerate() {
        return currentMount != null && currentMount.isValid() && storedStaminaValue < maxStoredStaminaValue;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        PlayerNpcMountDetection clone = new PlayerNpcMountDetection();
        clone.currentMount = currentMount;
        clone.storedStaminaValue = storedStaminaValue;
        return clone;
    }
}
