package eu.manuelgu.versionanalyze.listeners;

import eu.manuelgu.versionanalyze.VersionAnalyzePlugin;
import lombok.Getter;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import redis.clients.jedis.Jedis;

public class EventListener implements Listener {
    @Getter
    private final VersionAnalyzePlugin plugin;

    public EventListener(VersionAnalyzePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        getPlugin().getProxy().getScheduler().runAsync(getPlugin(), () -> {
            int protocolVersion = event.getPlayer().getPendingConnection().getVersion();
            String versionName = String.valueOf(protocolVersion);

            try (Jedis jedis = getPlugin().getRedis().getJedis().getResource()) {
                // Add uuid to set with name of protocolversion
                jedis.sadd(versionName, event.getPlayer().getUniqueId().toString());
            }
        });
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        getPlugin().getProxy().getScheduler().runAsync(getPlugin(), () -> {
            int protocolVersion = event.getPlayer().getPendingConnection().getVersion();
            String versionName = String.valueOf(protocolVersion);

            try (Jedis jedis = getPlugin().getRedis().getJedis().getResource()) {
                // Remove uuid from set
                jedis.srem(versionName, event.getPlayer().getUniqueId().toString());
            }
        });
    }
}
