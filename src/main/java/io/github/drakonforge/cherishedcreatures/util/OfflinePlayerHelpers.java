package io.github.drakonforge.cherishedcreatures.util;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.playerdata.DefaultPlayerStorageProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

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

    public static void saveOfflinePlayerRef(UUID playerUuid, Holder<EntityStore> holder) {
        DefaultPlayerStorageProvider.DEFAULT.getPlayerStorage().save(playerUuid, holder);
        CACHED_OFFLINE_PLAYER_REFS.put(playerUuid, holder);
    }
}
