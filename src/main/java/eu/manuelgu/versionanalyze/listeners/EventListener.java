package eu.manuelgu.versionanalyze.listeners;

import eu.manuelgu.versionanalyze.VersionAnalyzePlugin;
import eu.manuelgu.versionanalyze.util.APIUtil;
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

            String versionName = APIUtil.getVersionByProtocolVersion(protocolVersion);

            if (versionName != null) {
                try (Jedis jedis = getPlugin().getRedis().getJedis().getResource()) {
                    // Set uuid:versionName
                    jedis.set(event.getPlayer().getUniqueId().toString(), versionName);
                }
            }
        });
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        getPlugin().getProxy().getScheduler().runAsync(getPlugin(), () -> {
            try (Jedis jedis = getPlugin().getRedis().getJedis().getResource()) {
                // Remove value that contained version
                jedis.del(event.getPlayer().getUniqueId().toString());
            }
        });
    }
}
