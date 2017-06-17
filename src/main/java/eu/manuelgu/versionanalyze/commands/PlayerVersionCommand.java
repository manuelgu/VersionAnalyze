package eu.manuelgu.versionanalyze.commands;

import eu.manuelgu.versionanalyze.VersionAnalyzePlugin;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PlayerVersionCommand extends Command {
    @Getter
    private final VersionAnalyzePlugin plugin;

    public PlayerVersionCommand(VersionAnalyzePlugin plugin) {
        super("playerversion", "versionanalyze.playerversion", "");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        if (args.length != 1) {
            cs.sendMessage(new ComponentBuilder("Syntax: /playerversion <playerName>").color(ChatColor.RED).create());
            return;
        }

        String playerName = args[0];
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
        if (player == null) {
            cs.sendMessage(new ComponentBuilder("Player not found").color(ChatColor.RED).create());
            return;
        }

        int protocolVersion = player.getPendingConnection().getVersion();
        String version = String.valueOf(protocolVersion);

        cs.sendMessage(new ComponentBuilder(player.getName()).color(ChatColor.DARK_GREEN)
                .append(" is using Minecraft with protocol version ").color(ChatColor.GREEN)
                .append(version).color(ChatColor.DARK_GREEN)
                .create());
    }
}
