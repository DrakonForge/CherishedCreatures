package io.github.drakonforge.cherishedcreatures.ui.data;

import au.ellie.hyui.builders.PageBuilder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import java.util.List;
import javax.annotation.Nullable;

public record PetMenuContext(PageBuilder page, Store<EntityStore> store, Ref<EntityStore> ref,
                             PlayerRef playerRef, PlayerPetTracker petTracker,
                             List<PetUICard> petCards, PetUICard petCard, @Nullable Vector3d origin) {}
