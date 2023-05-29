package ce;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import redis.clients.jedis.Jedis;

/**
 * Created by xuwei
 */
public class TopNRedisSink extends RichSinkFunction<Tuple2<String,Long>> {
    private String host;
    private int port;
    private String key;

    private Jedis jedis = null;

    public TopNRedisSink(String host, int port, String key) {
        this.host = host;
        this.port = port;
        this.key = key;
    }

    /**
     * 初始化方法，只执行一次
     * @param parameters
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        this.jedis = new Jedis(host, port);
    }

    /**
     * 核心代码，来一条数据，此方法会执行一次
     * @param value
     * @param context
     * @throws Exception
     */
    public void invoke(Tuple2<String, Long> value, Context context) throws Exception {
        //给sortedset中的指定元素递增添加分值
        jedis.zincrby(key,value.f1,value.f0);
    }

    /**
     * 任务停止的时候会先调用此方法
     * 适合关闭资源连接
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        //关闭连接
        if(jedis!=null){
            jedis.close();
        }
    }
}
