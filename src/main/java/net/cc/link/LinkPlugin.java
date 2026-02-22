package net.cc.link;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import net.cc.core.api.model.CoreServer;
import net.cc.link.command.ServerCommand;
import net.cc.link.controller.ConfigController;
import net.cc.link.controller.ServiceController;
import net.cc.link.model.ConfigServer;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class.
 *
 * @since 2.0.0
 */
@Getter
public final class LinkPlugin extends JavaPlugin {

    private final ConfigController configController = new ConfigController(this);
    private final ServiceController serviceController = new ServiceController(this);

    private CoreServer serverType;

    @Override
    public void onLoad() {
        // Plugin load logic
        this.configController.initialize();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.serviceController.initialize();

        // Register plugin messaging through Velocity
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Validate core dependency
        if (this.serviceController.getCoreAPI() == null) {
            this.getComponentLogger().warn("Failed to get provider for cc-core, disabling plugin");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.serverType = this.serviceController.getServerType();

        this.registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerCommands() {
        // Register commands
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands registrar = event.registrar();

            // Register command for each server
            for (final ConfigServer configServer : this.configController.getConfigServers()) {
                new ServerCommand(this, configServer).register(registrar);
            }
        });
    }

}