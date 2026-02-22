package net.cc.link.controller;

import lombok.RequiredArgsConstructor;
import net.cc.link.LinkPlugin;
import net.cc.link.model.ConfigServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.nio.file.Files;
import java.nio.file.Path;
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

    private CommentedConfigurationNode configNode;
    private CommentedConfigurationNode messagesNode;

    // Initializes the controller
    public void initialize() {
        this.configNode = createNode("config.conf");
        this.messagesNode = createNode("messages.conf");
    }

    // Creates a HoconConfigurationLoader for the given file path
    private HoconConfigurationLoader createLoader(final Path path) {
        return HoconConfigurationLoader.builder()
                .path(path)
                .prettyPrinting(true)
                .build();
    }

    // Creates a CommentedConfigurationNode for the given file
    private CommentedConfigurationNode createNode(final String file) {
        final Path path = this.plugin.getDataPath().resolve(file);

        if (!Files.exists(path)) {
            this.plugin.getComponentLogger().info("Config file {} not found, creating it", file);
            this.plugin.saveResource(file, false);
        }

        final var loader = createLoader(path);

        try {
            return loader.load();
        } catch (final ConfigurateException ex) {
            this.plugin.getComponentLogger().error("Failed to load config file: {}", file, ex);
            return null;
        }
    }

    // Gets the timeout from the primary config
    public int getGlobalTimeout() {
        return this.configNode.node("timeout").getInt();
    }

    // Gets a list of servers from the primary config
    public List<ConfigServer> getConfigServers() {
        final ConfigurationNode node = this.configNode.node("servers");
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

    // Gets the command prefix from the message config
    public @NotNull String getPrefix() {
        final String prefix = this.messagesNode.node("prefix").getString();
        return prefix != null ? prefix : "";
    }

    // Gets a message from the message config
    public @NotNull Component getMessage(final String key, final TagResolver... resolvers) {
        final String message = this.messagesNode.node(key).getString();

        final List<TagResolver> tagResolvers = new ArrayList<>(List.of(resolvers));
        tagResolvers.add(Placeholder.parsed("prefix", getPrefix()));

        return message != null
                ? MiniMessage.miniMessage().deserialize(message, tagResolvers.toArray(TagResolver[]::new))
                : Component.text(key);
    }

}
