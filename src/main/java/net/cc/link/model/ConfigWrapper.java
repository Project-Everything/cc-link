package net.cc.link.model;

import lombok.Getter;
import net.cc.link.LinkPlugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Model class for a configuration file.
 *
 * @since 2.0.0
 */
public final class ConfigWrapper {

    private final LinkPlugin plugin;
    private final String fileName;
    private final HoconConfigurationLoader loader;

    @Getter
    private CommentedConfigurationNode node;

    // Constructor
    public ConfigWrapper(
            final LinkPlugin plugin,
            final String fileName
    ) {
        this.plugin = plugin;
        this.fileName = fileName;

        final Path path = plugin.getDataPath().resolve(fileName);

        // Check if file exists at path
        if (Files.notExists(path)) {
            plugin.getComponentLogger().info("Config file '{}' not found, creating it", fileName);
            plugin.saveResource(fileName, false);
        }

        // Create loader
        this.loader = HoconConfigurationLoader.builder()
                .path(path)
                .prettyPrinting(true)
                .build();
    }

    // Loads the config
    public void load() {
        try {
            this.node = this.loader.load();
        } catch (final ConfigurateException e) {
            this.plugin.getComponentLogger().error("Failed to load config '{}'", this.fileName, e);
            this.node = this.loader.createNode();
        }
    }

    // Saves the config
    public void save() {
        if (this.node == null) return;

        try {
            this.loader.save(this.node);
        } catch (final ConfigurateException e) {
            this.plugin.getComponentLogger().error("Failed to save config '{}'", this.fileName, e);
        }
    }

}
