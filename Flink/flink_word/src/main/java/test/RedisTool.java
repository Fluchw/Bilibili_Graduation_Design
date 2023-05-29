package test;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

public class RedisTool {
    private static JedisCluster createJedisCluster() {
        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("192.168.200.103", 7003));
        nodes.add(new HostAndPort("192.168.200.104", 7004));
        nodes.add(new HostAndPort("192.168.200.104", 7005));

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);

        return new JedisCluster(nodes, poolConfig);
    }

    public static class RedisClusterSink extends RichSinkFunction<Tuple2<String, String>> {
        private JedisCluster jedisCluster;
        private final int ttl;

        public RedisClusterSink(int ttl) {
            this.ttl = ttl;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            this.jedisCluster = createJedisCluster();
        }

        @Override
        public void invoke(Tuple2<String, String> value, Context context) throws Exception {
            System.out.println(value);
            jedisCluster.set(value.f0, value.f1);
            jedisCluster.expire(value.f0, ttl);

//            Set<String> keys = jedisCluster.hkeys("*");
//            System.out.println(jedisCluster.hkeys("*"));

        }

        @Override
        public void close() throws Exception {
            super.close();
            if (jedisCluster != null) {
                jedisCluster.close();
            }
        }
    }

    public static class RedisClusterSource extends RichSourceFunction<Tuple2<String, String>> {
        private JedisCluster jedisCluster;
        private volatile boolean isRunning = true;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            this.jedisCluster = createJedisCluster();
        }

        @Override
        public void run(SourceContext<Tuple2<String, String>> ctx) throws Exception {
            while (isRunning) {
                // 获取所有键
                Set<String> k = jedisCluster.hkeys("*");
                System.out.println(k);
                for (String key : k) {
                    // 获取键对应的值
                    String value = jedisCluster.get(key);
                    // 发射数据
                    ctx.collect(new Tuple2<>(key, value));
                }
                // 每隔一段时间执行一次查询
                Thread.sleep(500);
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }

        @Override
        public void close() throws Exception {
            super.close();
            if (jedisCluster != null) {
                jedisCluster.close();
            }
        }
    }
}
