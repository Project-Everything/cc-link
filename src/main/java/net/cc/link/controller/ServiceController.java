package net.cc.link.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.cc.core.api.Core;
import net.cc.core.api.CoreProvider;
import net.cc.core.api.model.CoreServer;
import net.cc.link.LinkPlugin;
import org.jetbrains.annotations.Nullable;

/**
 * Controller class for external services.
 *
 * @since 2.0.0
 */
@Getter
@RequiredArgsConstructor
public final class ServiceController {

    private final LinkPlugin plugin;
    private Core coreAPI;

    // Initializes the controller
    public void initialize() {
        this.coreAPI = CoreProvider.get();
    }

    // Gets the global ServerType from the core plugin
    public @Nullable CoreServer getServerType() {
        return this.coreAPI.getServer();
    }

}
