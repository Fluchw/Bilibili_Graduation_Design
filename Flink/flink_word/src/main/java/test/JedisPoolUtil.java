package test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * @program: Test
 * @description:
 * @author: Kylin Tian
 * @create: 2021-06-18 19:07
 */
public class JedisPoolUtil {
    private static volatile JedisCluster jedisPool = null;// 被volatile修饰的变量不会被本地线程缓存，对该变量的读写都是直接操作共享内存。

    private JedisPoolUtil() {
    }

    public static JedisCluster getJedisPoolInstance() {
        if (null == jedisPool) {
            synchronized (JedisPoolUtil.class) {
                if (null == jedisPool) {
                    JedisPoolConfig poolConfig = new JedisPoolConfig();
                    poolConfig.setMaxTotal(1000);
                    poolConfig.setMinIdle(2);
                    poolConfig.setMaxIdle(100);
                    poolConfig.setMaxWaitMillis(100 * 1000);
                    poolConfig.setTestOnBorrow(true);
                    poolConfig.setTestOnReturn(true);//
                    Set<HostAndPort> nodes = new HashSet<>();

                    String hadoop102="192.168.200.102";
                    String hadoop103="192.168.200.103";
                    String hadoop104="192.168.200.104";
                    nodes.add(new HostAndPort(hadoop102, 7000));
                    nodes.add(new HostAndPort(hadoop102, 7001));
                    nodes.add(new HostAndPort(hadoop103, 7002));
                    nodes.add(new HostAndPort(hadoop103, 7003));
                    nodes.add(new HostAndPort(hadoop104, 7004));
                    nodes.add(new HostAndPort(hadoop104, 7005));

//                    jedisPool = new JedisCluster(nodes, 10000, 10000, 100, poolConfig);
                }
            }
        }
        return jedisPool;
    }
}