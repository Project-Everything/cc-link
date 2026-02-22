package net.cc.link.command;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.cc.link.LinkPlugin;
import net.cc.link.model.ConfigServer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@RequiredArgsConstructor
public final class ServerCommand {

    private final LinkPlugin plugin;
    private final ConfigServer configServer;

    // Registers the command
    public void register(final Commands registrar) {
        final var command = Commands.literal(configServer.getServer())
                .executes(this::execute)
                .build();

        registrar.register(command, "Connect to " + configServer.getServer());
    }

    // Executes the command
    private int execute(final CommandContext<CommandSourceStack> context) {
        final CommandSender sender = context.getSource().getSender();

        // Check if the sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(this.plugin.getConfigController().getMessage("error-sender-not-player"));
            return 0;
        }

        // Check if player has permission
        if (!player.hasPermission(configServer.getPermission())) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>CC <white>» <gray>You do not have permission to use this command."));
            return 0;
        }

        // Check if player is already connected
        if (this.plugin.getServiceController().getCoreAPI() == null) {
            if (this.plugin.getServerType().toString().equals(this.configServer.getServer().toUpperCase())) {
                player.sendMessage(this.plugin.getConfigController().getMessage("command-server-current",
                        Placeholder.parsed("server", configServer.getServer())));
                return 0;
            }
        }

        // Check if the server is online
        if (isServerOnline()) {
            // Server is online, send player to server
            player.sendMessage(this.plugin.getConfigController().getMessage("command-server-connect",
                    Placeholder.parsed("server", configServer.getServer())));
            sendToServer(player);
        } else {
            // Server is offline
            player.sendMessage(this.plugin.getConfigController().getMessage("command-server-offline",
                    Placeholder.parsed("server", configServer.getServer())));
        }

        return Command.SINGLE_SUCCESS;
    }

    // Returns the current status of the server
    private boolean isServerOnline() {
        try (final Socket socket = new Socket()) {
            // Create socket connection
            socket.connect(new InetSocketAddress(configServer.getAddress(), configServer.getPort()),
                    this.plugin.getConfigController().getGlobalTimeout());
            return true;
        } catch (final IOException ignored) {
            return false;
        }
    }

    // Sends a player to the server
    private void sendToServer(final Player player) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(configServer.getServer());
        // Send plugin message through Velocity
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
}
