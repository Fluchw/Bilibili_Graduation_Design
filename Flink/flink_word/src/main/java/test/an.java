package test;

import DanmakuTools.EmotionAnalysis;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;
import java.util.UUID;

public class an {
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
        inputStream.print();


//        对数据进行处理
        DataStream<Tuple3<String, String,String>> stream = inputStream
                .map((MapFunction<String, Tuple3<String, String,String>>) value -> {
                    String uuid = UUID.randomUUID().toString();
                    String emo = EmotionAnalysis.emo(value);
                    return Tuple3.of(uuid, value,emo);
                });


        //        对数据进行处理
//        DataStream<String> stream = inputStream
//                .map((MapFunction<String, String>) value -> {
//                    return EmotionAnalysis.emo(value);
//                });


        // 输出到控制台
        stream.print();

        // 执行任务
        env.execute("Kafka to Redis");
    }
}
