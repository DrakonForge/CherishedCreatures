package io.github.drakonforge.cherishedcreatures;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.command.PetsCommand;
import io.github.drakonforge.cherishedcreatures.component.PetBondComponent;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.component.PetStateComponent;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.resource.PetUpdateQueue;
import io.github.drakonforge.cherishedcreatures.sensor.builder.BuilderSensorPetFollowMode;
import io.github.drakonforge.cherishedcreatures.system.EntityReceivePetUpdatesSystem;
import io.github.drakonforge.cherishedcreatures.system.RegisterPetComponentsSystem;
import io.github.drakonforge.cherishedcreatures.system.RegisterPlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.system.ResolvePetTrackerChangesSystem;
import io.github.drakonforge.cherishedcreatures.system.ResolvePetUpdatesOwnerSystem;
import io.github.drakonforge.cherishedcreatures.system.ResolvePetUpdatesPetSystem;
import javax.annotation.Nonnull;

/**
 * This class serves as the entrypoint for your plugin. Use the setup method to register into game registries or add
 * event listeners.
 */
public class CherishedCreaturesPlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static CherishedCreaturesPlugin instance;

    public static CherishedCreaturesPlugin get() {
        return instance;
    }

    private ResourceType<EntityStore, PetUpdateQueue> petUpdateQueueResourceType;

    private ComponentType<EntityStore, PlayerPetTracker> playerPetTrackerComponentType;
    private ComponentType<EntityStore, PetComponent> petComponentType;
    private ComponentType<EntityStore, PetStateComponent> petStateComponentType;
    private ComponentType<EntityStore, PetBondComponent> petBondComponentType;
    private ComponentType<EntityStore, PetTypeComponent> petTypeComponentType;

    public CherishedCreaturesPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        instance = this;
        LOGGER.atInfo().log("Setting up plugin " + this.getName() + " version " + this.getManifest().getVersion().toString());

        PetType.register();

        // When player logs in, grab all the existing pets
        // TODO: When entity loads, add to tracker
        // TODO: Apply pet changes on login
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            Player player = event.getPlayer();
            World world = player.getWorld();
            String playerName = player.getDisplayName();
            if (world == null) {
                LOGGER.atWarning().log("Failed to grab world for " + playerName);
                return;
            }
            Store<EntityStore> store = world.getEntityStore().getStore();
            Ref<EntityStore> playerRef = event.getPlayerRef();
            PlayerPetTracker playerPetTracker = store.getComponent(playerRef, PlayerPetTracker.getComponentType());
            if (playerPetTracker == null) {
                LOGGER.atWarning().log("Pet tracker not found for " + playerName);
                return;
            }
            int numFound = 0;
            for (int i = 0; i < playerPetTracker.getNumPetEntries(); ++i) {
                TrackedPetEntry entry = playerPetTracker.getPetEntry(i);
                Ref<EntityStore> existingEntity = world.getEntityStore().getRefFromUUID(entry.getUuid());
                if (existingEntity != null && existingEntity.isValid()) {
                    entry.setEntityRef(existingEntity);
                    entry.saveEntity(store);
                    numFound += 1;
                }
            }

            LOGGER.atInfo().log("Retrieved " + numFound + "/" + playerPetTracker.getNumPetEntries() + " pets for " + playerName);
            store.getResource(this.getPetUpdateQueueResourceType()).deliverUpdatesForPlayer(store, playerRef);
        });

        // Commands
        this.getCommandRegistry().registerCommand(new PetsCommand());

        // Components
        ComponentRegistryProxy<EntityStore> entityStoreRegistry = this.getEntityStoreRegistry();
        this.petUpdateQueueResourceType = entityStoreRegistry.registerResource(PetUpdateQueue.class, "PetUpdateQueue", PetUpdateQueue.CODEC);

        this.playerPetTrackerComponentType = entityStoreRegistry.registerComponent(PlayerPetTracker.class, "PlayerPetTracker", PlayerPetTracker.CODEC);
        this.petComponentType = entityStoreRegistry.registerComponent(PetComponent.class, "PetComponent", PetComponent.CODEC);
        this.petStateComponentType = entityStoreRegistry.registerComponent(PetStateComponent.class, "PetStateComponent", PetStateComponent.CODEC);
        this.petBondComponentType = entityStoreRegistry.registerComponent(PetBondComponent.class, "PetBondComponent", PetBondComponent.CODEC);
        this.petTypeComponentType = entityStoreRegistry.registerComponent(PetTypeComponent.class, "PetType", PetTypeComponent.CODEC);

        // Systems
        entityStoreRegistry.registerSystem(new RegisterPlayerPetTracker());
        entityStoreRegistry.registerSystem(new ResolvePetTrackerChangesSystem());
        entityStoreRegistry.registerSystem(new EntityReceivePetUpdatesSystem());
        entityStoreRegistry.registerSystem(new RegisterPetComponentsSystem());
        entityStoreRegistry.registerSystem(new ResolvePetUpdatesPetSystem());
        entityStoreRegistry.registerSystem(new ResolvePetUpdatesOwnerSystem());

        // Sensors (Core Components)
        NPCPlugin.get().registerCoreComponentType("PetFollowMode", BuilderSensorPetFollowMode::new);
    }

    @Override
    protected void start() {
        LOGGER.atInfo().log("Starting plugin " + this.getName());
    }

    public ComponentType<EntityStore, PlayerPetTracker> getPlayerPetTrackerComponentType() {
        return this.playerPetTrackerComponentType;
    }

    public ComponentType<EntityStore, PetStateComponent> getPetStateComponentType() {
        return this.petStateComponentType;
    }

    public ComponentType<EntityStore, PetComponent> getPetComponentType() {
        return this.petComponentType;
    }

    public ComponentType<EntityStore, PetBondComponent> getPetBondComponentType() {
       return this.petBondComponentType;
    }

    public ResourceType<EntityStore, PetUpdateQueue> getPetUpdateQueueResourceType() {
        return this.petUpdateQueueResourceType;
    }

    public ComponentType<EntityStore, PetTypeComponent> getPetTypeComponentType() {
        return this.petTypeComponentType;
    }
}