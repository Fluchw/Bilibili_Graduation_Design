package DanmakuUtils;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class RedisClusterUtils {
    private static RedisClusterClient createRedisClusterClient() {
        // 创建RedisURI列表
        List<RedisURI> redisURIs = Arrays.asList(
                RedisURI.create(DanmakuUtils.DanmakuConfig.REDIS_URI_1),
                RedisURI.create(DanmakuUtils.DanmakuConfig.REDIS_URI_2),
                RedisURI.create(DanmakuUtils.DanmakuConfig.REDIS_URI_3)
        );
        // 创建RedisClusterClient实例
        return RedisClusterClient.create(redisURIs);
    }

    public static class RedisClusterSink extends RichSinkFunction<Tuple2<String, String>> {
        private RedisClusterClient clusterClient;
        private StatefulRedisClusterConnection<String, String> connection;
        private RedisAdvancedClusterCommands<String, String> syncCommands;
        private final int ttl;

//        数据缓存保留时间
        public RedisClusterSink(int ttl) {
            this.ttl = ttl;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            // 创建RedisClusterClient实例
            clusterClient = createRedisClusterClient();
            // 连接到Redis集群
            connection = clusterClient.connect();
            // 获取同步命令
            syncCommands = connection.sync();
        }

        @Override
        public void invoke(Tuple2<String, String> value, Context context) {
//            System.out.println(value);
            syncCommands.setex(value.f0, ttl, value.f1);
        }

        @Override
        public void close() throws Exception {
            super.close();
            if (connection != null) {
                connection.close();
            }
            if (clusterClient != null) {
                clusterClient.shutdown();
            }
        }
    }

    public static class RedisClusterSource extends RichSourceFunction<Tuple2<String, String>> {
        private RedisClusterClient clusterClient;
        private StatefulRedisClusterConnection<String, String> connection;
        private RedisAdvancedClusterCommands<String, String> syncCommands;
        private volatile boolean isRunning = true;
        private Map<String, String> lastData;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            // 创建RedisClusterClient实例
            clusterClient = createRedisClusterClient();
            // 连接到Redis集群
            connection = clusterClient.connect();
            // 获取同步命令
            syncCommands = connection.sync();
            // 初始化lastData
            lastData = new HashMap<>();
        }

        @Override
        public void run(SourceContext<Tuple2<String, String>> ctx) {
            while (isRunning) {
                // 获取所有键
                List<String> keys = syncCommands.keys("*");
                for (String key : keys) {
                    // 获取键对应的值
                    String value = syncCommands.get(key);
                    // 检查数据是否发生了变化
                    if (value != null && !value.equals(lastData.get(key))){
                        // 发射数据
                        ctx.collect(new Tuple2<>(key, value));
                        // 更新lastData
                        lastData.put(key, value);
                    }
                }
                // 每隔一段时间执行一次查询
//                Thread.sleep(500);
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }

        @Override
        public void close() throws Exception {
            super.close();
            if (connection != null) {
                connection.close();
            }
            if (clusterClient != null) {
                clusterClient.shutdown();
            }
        }
    }


}
