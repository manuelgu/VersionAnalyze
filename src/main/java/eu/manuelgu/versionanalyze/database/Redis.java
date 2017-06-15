package eu.manuelgu.versionanalyze.database;

import lombok.Getter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Represents a connection to a Redis database over Jedis
 */
public class Redis {
    private String host;
    private int port;
    private int timeout;
    private String auth;

    @Getter
    private JedisPool jedis;

    public Redis(String host, int port, int timeout, String auth) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.auth = auth;
    }

    public void initialize() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMinIdle(8);
        poolConfig.setMaxTotal(32);

        if (!auth.isEmpty()) {
            jedis = new JedisPool(poolConfig, host, port, timeout, auth);
        } else {
            jedis = new JedisPool(poolConfig, host, port, timeout);
        }
    }
}
