package net.cc.link.model;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Getter
@ConfigSerializable
public final class ConfigServer {

    private final String server;
    private final String address;
    private final int port;
    private final String permission;

    // Constructor
    public ConfigServer() {
        this.server = "lobby";
        this.address = "172.18.0.1";
        this.port = 30066;
        this.permission = "ae.server.lobby";
    }
}
