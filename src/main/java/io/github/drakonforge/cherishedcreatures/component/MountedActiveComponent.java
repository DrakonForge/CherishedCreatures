package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

// A marker to determine whether the player has an active mount
public class MountedActiveComponent implements Component<EntityStore> {
    public static MountedActiveComponent INSTANCE = new MountedActiveComponent();

    public static ComponentType<EntityStore, MountedActiveComponent> getComponentType() {
        return CherishedCreaturesPlugin.get().getMountedActiveComponentType();
    }

    protected MountedActiveComponent() {}

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return INSTANCE;
    }
}
