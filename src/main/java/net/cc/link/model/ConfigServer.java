package net.cc.link.model;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for a server.
 *
 * @since 2.0.0
 */
@Getter
@ConfigSerializable
public final class ConfigServer {

    private final String server;
    private final String address;
    private final int port;
    private final String permission;
    private final List<String> aliases;

    // Constructor
    public ConfigServer() {
        this.server = "lobby";
        this.address = "172.18.0.1";
        this.port = 30066;
        this.permission = "ae.server.lobby";
        this.aliases = new ArrayList<>();
    }
}
