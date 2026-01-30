package io.github.drakonforge.cherishedcreatures;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;

/**
 * This class serves as the entrypoint for your plugin. Use the setup method to register into game registries or add
 * event listeners.
 */
public class CherishedCreaturesPlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static CherishedCreaturesPlugin instance;

    public static CherishedCreaturesPlugin getInstance() {
        return instance;
    }

    public CherishedCreaturesPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        instance = this;
        LOGGER.atInfo().log("Setting up plugin " + this.getName() + " version " + this.getManifest().getVersion().toString());

        // Run /test to confirm example plugin is working
        this.getCommandRegistry().registerCommand(new ExampleCommand(this.getName(), this.getManifest().getVersion().toString()));
    }

    @Override
    protected void start() {
        LOGGER.atInfo().log("Starting plugin " + this.getName());
    }
}