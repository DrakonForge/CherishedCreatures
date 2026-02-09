package io.github.drakonforge.cherishedcreatures.util;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.playerdata.DefaultPlayerStorageProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OfflinePlayerHelpers {

    private static final Map<UUID, Holder<EntityStore>> CACHED_OFFLINE_PLAYER_REFS = new LinkedHashMap<>();

    public static Map<UUID, Holder<EntityStore>> getCachedOfflinePlayerRefs() {
        return CACHED_OFFLINE_PLAYER_REFS;
    }

    public static boolean isOnline(UUID playerUuid) {
        return Universe.get().getPlayer(playerUuid) != null;
    }

    public static Holder<EntityStore> getOfflinePlayerRef(UUID playerUuid) {
        Holder<EntityStore> cachedRef = CACHED_OFFLINE_PLAYER_REFS.get(playerUuid);
        if (cachedRef != null) {
            return cachedRef;
        }
        Holder<EntityStore> newRef;
        try {
            newRef = DefaultPlayerStorageProvider.DEFAULT.getPlayerStorage().load(playerUuid).get();
        } catch (Exception e) {
            return null;
        }
        if (newRef != null) {
            CACHED_OFFLINE_PLAYER_REFS.put(playerUuid, newRef);
        }
        return newRef;
    }

    @Nullable
    public static <T extends Component<EntityStore>> T getComponent(@Nonnull Store<EntityStore> store, @Nonnull UUID playerUuid, @Nonnull ComponentType<EntityStore, T> componentType) {
        PlayerRef playerRef = Universe.get().getPlayer(playerUuid);
        if (playerRef != null) {
            Ref<EntityStore> onlinePlayerRef = playerRef.getReference();
            if (onlinePlayerRef != null) {
                return store.getComponent(onlinePlayerRef, componentType);
            }
        }
        Holder<EntityStore> offlinePlayer = getOfflinePlayerRef(playerUuid);
        if (offlinePlayer == null) {
            return null;
        }
        return offlinePlayer.getComponent(componentType);
    }

    public static void saveOfflinePlayerRef(UUID playerUuid, Holder<EntityStore> holder) {
        DefaultPlayerStorageProvider.DEFAULT.getPlayerStorage().save(playerUuid, holder);
        CACHED_OFFLINE_PLAYER_REFS.put(playerUuid, holder);
    }

    public static void saveIfOffline(@Nonnull UUID playerUuid) {
        if (Universe.get().getPlayer(playerUuid) != null) {
            return;
        }
        Holder<EntityStore> holder = getOfflinePlayerRef(playerUuid);
        if (holder != null) {
            DefaultPlayerStorageProvider.DEFAULT.getPlayerStorage().save(playerUuid, holder);
        }
    }
}
