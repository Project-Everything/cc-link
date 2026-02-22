package net.cc.link.controller;

import lombok.RequiredArgsConstructor;
import net.cc.link.Constants;
import net.cc.link.LinkPlugin;
import net.cc.link.model.ConfigServer;
import net.cc.link.model.ConfigWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller class for config files.
 *
 * @since 2.0.0
 */
@RequiredArgsConstructor
public final class ConfigController {

    private final LinkPlugin plugin;

    private ConfigWrapper primaryConfig;
    private ConfigWrapper messagesConfig;

    // Initializes the controller.
    public void initialize() {
        this.primaryConfig = new ConfigWrapper(this.plugin, Constants.CONFIG_PRIMARY);
        this.messagesConfig = new ConfigWrapper(this.plugin, Constants.CONFIG_MESSAGES);

        this.load();
    }

    // Loads the configs
    public void load() {
        this.primaryConfig.load();
        this.messagesConfig.load();
    }

    // Saves the configs
    public void save() {
        this.primaryConfig.save();
        this.messagesConfig.save();
    }

    // Gets the global timeout
    public int getGlobalTimeout() {
        return this.primaryConfig.getNode().node("timeout").getInt();
    }

    // Gets the list of servers
    public List<ConfigServer> getConfigServers() {
        final ConfigurationNode node = this.primaryConfig.getNode().node("servers");
        final List<ConfigServer> configServers = new ArrayList<>();

        if (node.isMap()) {
            final Map<Object, ? extends ConfigurationNode> serverMap = node.childrenMap();

            // Iterate over children in server map
            serverMap.forEach((key, value) -> {
                try {
                    final ConfigServer server = value.get(ConfigServer.class);
                    configServers.add(server);
                } catch (SerializationException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return configServers;
    }

    // Gets the command prefix
    public @NotNull String getPrefix() {
        final String prefix = this.messagesConfig.getNode().node("prefix").getString();

        return prefix != null
                ? prefix
                : "";
    }

    // Gets a message as a Component
    public @NotNull Component getMessage(final String key, final TagResolver... resolvers) {
        final String message = this.messagesConfig.getNode().node(key).getString();

        final List<TagResolver> tagResolvers = new ArrayList<>(List.of(resolvers));
        tagResolvers.add(Placeholder.parsed("prefix", getPrefix()));

        if (message != null) {
            return MiniMessage.miniMessage().deserialize(message, tagResolvers.toArray(TagResolver[]::new))
                    .decorationIfAbsent(
                            TextDecoration.ITALIC,
                            TextDecoration.State.FALSE
                    );
        }

        return Component.text(key);
    }

}
