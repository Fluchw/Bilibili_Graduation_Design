package ce;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 基于Redis连接池提取Redis工具类
 * Created by xuwei
 */
public class RedisUtils {
    //私有化构造函数，禁止new
    private RedisUtils(){}

    private static JedisPool jedisPool = null;

    //获取连接
    public static synchronized Jedis getJedis(){
        if(jedisPool==null){
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxIdle(10);
            poolConfig.setMaxTotal(100);
            poolConfig.setMaxWaitMillis(2000);
            poolConfig.setTestOnBorrow(true);
            jedisPool = new JedisPool(poolConfig, "127.0.0.1", 6380);
        }
        return jedisPool.getResource();
    }

    //向连接池返回连接
    public static void returnResource(Jedis jedis){
        jedis.close();
    }
}
