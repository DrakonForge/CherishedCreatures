package io.github.drakonforge.cherishedcreatures.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.CherishedCreaturesPlugin;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class MountHandlingComponent implements Component<EntityStore> {

    @Nonnull
    private PetType mountedPetType;

    public MountHandlingComponent() {
        mountedPetType = PetType.DEFAULT;
    }

    public MountHandlingComponent(@NonNullDecl PetType mountedPetType) {
        this.mountedPetType = mountedPetType;
    }

    public static ComponentType<EntityStore, MountHandlingComponent> getComponentType() {
        return CherishedCreaturesPlugin.get().getMountHandlingComponentType();
    }

    @NonNullDecl
    public PetType getMountedPetType() {
        return mountedPetType;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        MountHandlingComponent clone = new MountHandlingComponent();
        clone.mountedPetType = mountedPetType;
        return clone;
    }
}
