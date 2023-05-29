package test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

/**
 * 1:统计全站双11当天实时GMV
 * 2:统计实时销量TopN的商品品类
 * Created by xuwei
 */
public class red {
    public static void main(String[] args) throws Exception {
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置 Kafka 连接参数
        String topic = "DANMU_MSG";
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "hadoop102:9092,hadoop103:9092,hadoop104:9092");
        properties.setProperty("group.id", "DANMU_MSG");

        // 创建 Kafka 消费者
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(topic, new SimpleStringSchema(), properties);

        // 添加数据源
        DataStream<String> inputStream = env.addSource(consumer);

//        对数据进行处理
        DataStream<Tuple2<String, String>> stream = inputStream
                .map((MapFunction<String, Tuple2<String, String>>) value -> {
                    JSONObject json = JSON.parseObject(value);
                    String id_str = json.getString("id_str");
                    return Tuple2.of(id_str, value);
                });


        //写入redis
        stream.addSink(new RedisTool.RedisClusterSink(600));


        // 输出到控制台
//        stream.print();

        // 执行任务
        env.execute("Kafka to Redis");

    }


}
