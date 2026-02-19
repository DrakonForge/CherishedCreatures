package io.github.drakonforge.cherishedcreatures;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.npc.NPCPlugin;
import io.github.drakonforge.cherishedcreatures.asset.PetActivity;
import io.github.drakonforge.cherishedcreatures.asset.PetType;
import io.github.drakonforge.cherishedcreatures.command.PetsCommand;
import io.github.drakonforge.cherishedcreatures.component.MountHandlingComponent;
import io.github.drakonforge.cherishedcreatures.component.MountStatusMetersComponent;
import io.github.drakonforge.cherishedcreatures.component.MountedActiveComponent;
import io.github.drakonforge.cherishedcreatures.component.PetBondComponent;
import io.github.drakonforge.cherishedcreatures.component.PetComponent;
import io.github.drakonforge.cherishedcreatures.component.PetStateComponent;
import io.github.drakonforge.cherishedcreatures.component.PetTypeComponent;
import io.github.drakonforge.cherishedcreatures.component.PlayerNpcMountDetection;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;

import io.github.drakonforge.cherishedcreatures.corecomponents.builder.BuilderActionOpenPetMenu;
import io.github.drakonforge.cherishedcreatures.corecomponents.builder.BuilderActionTriggerPetActivity;
import io.github.drakonforge.cherishedcreatures.corecomponents.builder.BuilderEntityFilterPetOwner;
import io.github.drakonforge.cherishedcreatures.corecomponents.builder.BuilderSensorPetOwner;
import io.github.drakonforge.cherishedcreatures.data.TrackedPetEntry;
import io.github.drakonforge.cherishedcreatures.resource.PetUpdateQueue;
import io.github.drakonforge.cherishedcreatures.corecomponents.builder.BuilderSensorBondingLevel;
import io.github.drakonforge.cherishedcreatures.corecomponents.builder.BuilderSensorPetFollowMode;
import io.github.drakonforge.cherishedcreatures.system.*;

import io.github.drakonforge.cherishedcreatures.system.mount.AddMountHandlingSystem;
import io.github.drakonforge.cherishedcreatures.system.mount.DetectNpcMountSystem;
import io.github.drakonforge.cherishedcreatures.system.mount.EnsureMountStatusMetersSystem;
import io.github.drakonforge.cherishedcreatures.system.mount.HideMountStatusMetersSystem;
import io.github.drakonforge.cherishedcreatures.system.mount.MountHandlingTickingSystem;
import io.github.drakonforge.cherishedcreatures.system.mount.RegenerateStoredStaminaSystem;
import io.github.drakonforge.cherishedcreatures.system.mount.RegisterNpcMountDetectionSystem;
import io.github.drakonforge.cherishedcreatures.system.mount.RemoveMountHandlingSystem;
import io.github.drakonforge.cherishedcreatures.system.mount.RestoreMountStaminaSystem;
import io.github.drakonforge.cherishedcreatures.system.mount.ShowMountStatusMetersSystem;
import io.github.drakonforge.cherishedcreatures.system.mount.UpdateMountStatusMetersSystem;
import io.github.drakonforge.cherishedcreatures.system.mount.UseMountStaminaSystem;
import javax.annotation.Nonnull;

/**
 * This class serves as the entrypoint for your plugin. Use the setup method to register into game
 * registries or add event listeners.
 */
public class CherishedCreaturesPlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static CherishedCreaturesPlugin instance;

    public static CherishedCreaturesPlugin get() {
        return instance;
    }

    private final Config<CherishedCreaturesConfig> config;
    private ResourceType<EntityStore, PetUpdateQueue> petUpdateQueueResourceType;

    private ComponentType<EntityStore, PlayerPetTracker> playerPetTrackerComponentType;
    private ComponentType<EntityStore, PetComponent> petComponentType;
    private ComponentType<EntityStore, PetStateComponent> petStateComponentType;
    private ComponentType<EntityStore, PetBondComponent> petBondComponentType;
    private ComponentType<EntityStore, PetTypeComponent> petTypeComponentType;
    private ComponentType<EntityStore, MountHandlingComponent> mountHandlingComponentType;
    private ComponentType<EntityStore, PlayerNpcMountDetection> playerNpcMountDetectionComponentType;
    private ComponentType<EntityStore, MountStatusMetersComponent> mountStatusMetersComponentType;
    private ComponentType<EntityStore, MountedActiveComponent> mountedActiveComponentType;

    private SystemGroup<EntityStore> filterBondingXpEventGroup;
    private SystemGroup<EntityStore> inspectBondingXpEventGroup;

    public CherishedCreaturesPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        config = this.withConfig("CherishedCreatures", CherishedCreaturesConfig.CODEC);
    }

    @Override
    protected void setup() {
        instance = this;
        LOGGER.atInfo()
                .log("Setting up plugin " + this.getName() + " version " + this.getManifest()
                        .getVersion()
                        .toString());

        // Custom Assets
        PetType.register();
        PetActivity.register();

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
            PlayerPetTracker playerPetTracker = store.getComponent(playerRef,
                    PlayerPetTracker.getComponentType());
            if (playerPetTracker != null) {
                int numFound = 0;
                for (int i = 0; i < playerPetTracker.getNumPetEntries(); ++i) {
                    TrackedPetEntry entry = playerPetTracker.getPetEntry(i);
                    Ref<EntityStore> existingEntity = world.getEntityStore()
                            .getRefFromUUID(entry.getUuid());
                    if (existingEntity != null && existingEntity.isValid()) {
                        entry.saveEntityFromRef(store, existingEntity);
                        numFound += 1;
                    }
                }

                LOGGER.atInfo()
                        .log("Retrieved " + numFound + "/" + playerPetTracker.getNumPetEntries()
                                + " pets for " + playerName);
                store.getResource(this.getPetUpdateQueueResourceType())
                        .deliverUpdatesForPlayer(store, playerRef);
            } else {
                LOGGER.atWarning().log("Pet tracker not found for " + playerName);
            }

            MountStatusMetersComponent statusMeters = store.getComponent(playerRef,
                    MountStatusMetersComponent.getComponentType());
            if (statusMeters != null) {
                statusMeters.getStaminaMeter().hide();
                statusMeters.getHealthMeter().hide();
            } else {
                LOGGER.atWarning().log("Mount status meter component not found for " + playerName);
            }

        });

        // Commands
        this.getCommandRegistry().registerCommand(new PetsCommand());

        // Components
        ComponentRegistryProxy<EntityStore> entityStoreRegistry = this.getEntityStoreRegistry();
        this.petUpdateQueueResourceType = entityStoreRegistry.registerResource(PetUpdateQueue.class,
                "PetUpdateQueue", PetUpdateQueue.CODEC);

        this.playerPetTrackerComponentType = entityStoreRegistry.registerComponent(
                PlayerPetTracker.class, "PlayerPetTracker", PlayerPetTracker.CODEC);
        this.petComponentType = entityStoreRegistry.registerComponent(PetComponent.class,
                "PetComponent", PetComponent.CODEC);
        this.petStateComponentType = entityStoreRegistry.registerComponent(PetStateComponent.class,
                "PetStateComponent", PetStateComponent.CODEC);
        this.petBondComponentType = entityStoreRegistry.registerComponent(PetBondComponent.class,
                "PetBondComponent", PetBondComponent.CODEC);
        this.petTypeComponentType = entityStoreRegistry.registerComponent(PetTypeComponent.class,
                "PetType", PetTypeComponent.CODEC);
        this.mountHandlingComponentType = entityStoreRegistry.registerComponent(
                MountHandlingComponent.class, MountHandlingComponent::new);
        this.playerNpcMountDetectionComponentType = entityStoreRegistry.registerComponent(
                PlayerNpcMountDetection.class, PlayerNpcMountDetection::new);
        this.mountStatusMetersComponentType = entityStoreRegistry.registerComponent(
                MountStatusMetersComponent.class, MountStatusMetersComponent::new);
        this.mountedActiveComponentType = entityStoreRegistry.registerComponent(MountedActiveComponent.class, () -> MountedActiveComponent.INSTANCE);

        this.filterBondingXpEventGroup = entityStoreRegistry.registerSystemGroup();
        this.inspectBondingXpEventGroup = entityStoreRegistry.registerSystemGroup();

        // Systems
        entityStoreRegistry.registerSystem(new RegisterPlayerPetTracker());
        entityStoreRegistry.registerSystem(new PetUpdateTrackerSystem());
        entityStoreRegistry.registerSystem(new RegisterPetComponentsSystem());
        entityStoreRegistry.registerSystem(new ResolvePetUpdatesPetSystem());
        entityStoreRegistry.registerSystem(new ResolvePetUpdatesOwnerSystem());
        entityStoreRegistry.registerSystem(new PetActivityCooldownSystem());
        entityStoreRegistry.registerSystem(new RegisterDefaultPetTypeSystem());
        entityStoreRegistry.registerSystem(new HandlePetActivityEventSystem());
        entityStoreRegistry.registerSystem(new OnPetDeathSystem());
        entityStoreRegistry.registerSystem(new ApplyBondingXpSystem());
        entityStoreRegistry.registerSystem(new NotifyBondingXpSystem());
        entityStoreRegistry.registerSystem(new UpdateBondingLevelSystem());
        entityStoreRegistry.registerSystem(new UpdateExplorationMarkerSystem());
        // Mount Handling
        entityStoreRegistry.registerSystem(new RegisterNpcMountDetectionSystem());
        entityStoreRegistry.registerSystem(new DetectNpcMountSystem());
        entityStoreRegistry.registerSystem(new AddMountHandlingSystem());
        entityStoreRegistry.registerSystem(new RemoveMountHandlingSystem());
        entityStoreRegistry.registerSystem(new MountHandlingTickingSystem());
        entityStoreRegistry.registerSystem(new UseMountStaminaSystem());
        entityStoreRegistry.registerSystem(new RestoreMountStaminaSystem());
        entityStoreRegistry.registerSystem(new RegenerateStoredStaminaSystem());
        entityStoreRegistry.registerSystem(new EnsureMountStatusMetersSystem());
        entityStoreRegistry.registerSystem(new ShowMountStatusMetersSystem());
        entityStoreRegistry.registerSystem(new HideMountStatusMetersSystem());
        entityStoreRegistry.registerSystem(new UpdateMountStatusMetersSystem());

        // Core Components
        NPCPlugin npcPlugin = NPCPlugin.get();
        npcPlugin.registerCoreComponentType("BondingLevel", BuilderSensorBondingLevel::new);
        npcPlugin.registerCoreComponentType("PetFollowMode", BuilderSensorPetFollowMode::new);
        npcPlugin.registerCoreComponentType("PetOwner", BuilderSensorPetOwner::new);
        npcPlugin.registerCoreComponentType("OpenPetMenu", BuilderActionOpenPetMenu::new);
        npcPlugin.registerCoreComponentType("PetOwner", BuilderEntityFilterPetOwner::new);
        npcPlugin.registerCoreComponentType("TriggerPetActivity",
                BuilderActionTriggerPetActivity::new);

        config.save();
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

    public ComponentType<EntityStore, PetTypeComponent> getPetTypeComponentType() {
        return this.petTypeComponentType;
    }

    public ComponentType<EntityStore, MountHandlingComponent> getMountHandlingComponentType() {
        return mountHandlingComponentType;
    }

    public ComponentType<EntityStore, PlayerNpcMountDetection> getPlayerNpcMountDetectionComponentType() {
        return playerNpcMountDetectionComponentType;
    }

    public ComponentType<EntityStore, MountStatusMetersComponent> getMountStatusMetersComponentType() {
        return mountStatusMetersComponentType;
    }

    public ComponentType<EntityStore, MountedActiveComponent> getMountedActiveComponentType() {
        return mountedActiveComponentType;
    }

    public ResourceType<EntityStore, PetUpdateQueue> getPetUpdateQueueResourceType() {
        return this.petUpdateQueueResourceType;
    }

    public SystemGroup<EntityStore> getFilterBondingXpEventGroup() {
        return filterBondingXpEventGroup;
    }

    public SystemGroup<EntityStore> getInspectBondingXpEventGroup() {
        return inspectBondingXpEventGroup;
    }

    public Config<CherishedCreaturesConfig> getConfig() {
        return config;
    }
}