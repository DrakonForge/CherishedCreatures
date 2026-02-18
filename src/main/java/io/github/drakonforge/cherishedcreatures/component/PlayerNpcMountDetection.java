package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PlayerNpcMountDetection implements Component<EntityStore> {

    @Nullable
    private Ref<EntityStore> currentMount = null;

    public static ComponentType<EntityStore, PlayerNpcMountDetection> getComponentType() {
        return CherishedCreaturesPlugin.get().getPlayerNpcMountDetectionComponentType();
    }

    public void setCurrentMount(@Nullable Ref<EntityStore> currentMount) {
        this.currentMount = currentMount;
    }

    @Nullable
    public Ref<EntityStore> getCurrentMount() {
        return currentMount;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        PlayerNpcMountDetection clone = new PlayerNpcMountDetection();
        clone.currentMount = currentMount;
        return clone;
    }
}
