package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class MountHandlingComponent implements Component<EntityStore> {

    public static ComponentType<EntityStore, MountHandlingComponent> getComponentType() {
        return CherishedCreaturesPlugin.get().getMountHandlingComponentType();
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        MountHandlingComponent clone = new MountHandlingComponent();
        return clone;
    }
}
