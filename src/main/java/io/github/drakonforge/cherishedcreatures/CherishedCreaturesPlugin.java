package io.github.drakonforge.cherishedcreatures;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.cherishedcreatures.asset.PetConfigAsset;
import io.github.drakonforge.cherishedcreatures.component.PetBondComponent;
import io.github.drakonforge.cherishedcreatures.component.PetOwnerComponent;
import io.github.drakonforge.cherishedcreatures.component.PetStateComponent;
import io.github.drakonforge.cherishedcreatures.component.PlayerPetTracker;
import io.github.drakonforge.cherishedcreatures.system.RegisterPlayerPetTracker;
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

    private ComponentType<EntityStore, PlayerPetTracker> playerPetTrackerComponentType;
    private ComponentType<EntityStore, PetStateComponent> petStateComponentType;
    private ComponentType<EntityStore, PetOwnerComponent> petOwnerComponentType;
    private ComponentType<EntityStore, PetBondComponent> petBondComponentType;

    public CherishedCreaturesPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        instance = this;
        LOGGER.atInfo().log("Setting up plugin " + this.getName() + " version " + this.getManifest().getVersion().toString());

        PetConfigAsset.register();

        // Run /test to confirm example plugin is working
        this.getCommandRegistry().registerCommand(new ExampleCommand(this.getName(), this.getManifest().getVersion().toString()));

        // Components
        ComponentRegistryProxy<EntityStore> entityStoreRegistry = this.getEntityStoreRegistry();
        this.playerPetTrackerComponentType = entityStoreRegistry.registerComponent(PlayerPetTracker.class, "PlayerPetTracker", PlayerPetTracker.CODEC);
        this.petStateComponentType = entityStoreRegistry.registerComponent(PetStateComponent.class, "PetStateComponent", PetStateComponent.CODEC);
        this.petOwnerComponentType = entityStoreRegistry.registerComponent(PetOwnerComponent.class, "PetOwnerComponent", PetOwnerComponent.CODEC);
        this.petBondComponentType = entityStoreRegistry.registerComponent(PetBondComponent.class, "PetBondComponent", PetBondComponent.CODEC);

        // Systems
        entityStoreRegistry.registerSystem(new RegisterPlayerPetTracker());
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

    public ComponentType<EntityStore, PetOwnerComponent> getPetOwnerComponentType() {
        return this.petOwnerComponentType;
    }

    public ComponentType<EntityStore, PetBondComponent> getPetBondComponentType() {
       return this.petBondComponentType;
    }
}