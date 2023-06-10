package bilibili;

import DanmakuUtils.ColorConverterUtils;
import DanmakuUtils.DanmakuConfig;
import DanmakuUtils.RedisClusterUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

import static DanmakuUtils.EmotionUtils.emotionCount;

public class Kafka2Redis {
    public static void main(String[] args) throws Exception {
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置 Kafka 连接参数
        String topic = "DANMU_MSG";
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", DanmakuConfig.KAFKA_BOOTSTRAP_SERVERS);
        properties.setProperty("group.id", "DANMU_MSG");

        // 创建 Kafka 消费者
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(topic, new SimpleStringSchema(), properties);

        // 添加数据源
        DataStream<String> inputStream = env.addSource(consumer);

//        EmotionUtils e=new EmotionUtils();

//        System.out.println(e.emotionCount(text));

//        对弹幕进行情感分析
//        DataStream<String> emoStream = inputStream
//                .map((MapFunction<String, String>) value -> {
//                    return EmotionAnalysis.emo(value);
//                });
//        emoStream.print();


        //        注意:可能收到warning,修复后可能会出现问题
//        warning:匿名 new MapFunction<String, Tuple2<String, String>>() 可替换为 lambda
        //对数据进行处理
        DataStream<Tuple2<String, String>> stream = inputStream
                .map(new MapFunction<String, Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> map(String value) throws Exception {
                        try {
                            JSONObject json = JSON.parseObject(value);

                            //转换颜色为中文
                            String color_10=json.getString("color");
                            String color = ColorConverterUtils.color2cn(Integer.parseInt(color_10));
                            json.put("color",color);

                            //因为es中的时间戳是默认精确到毫秒,而B站弹幕的时间戳是精确到秒级,所以需要转换
                            String timestampString = json.getString("timestamp");
                            long timestamp = Long.parseLong(timestampString) * 1000;
                            json.put("timestamp",timestamp);

                            String id_str = json.getString("id_str");
                            String content = json.getString("content");
//                            调用情感分析
                            String emo = emotionCount(content);

                            JSONObject obj = JSON.parseObject(emo);

                            json.fluentPutAll(obj);

                            String mergedJson = JSON.toJSONString(json);

                            return Tuple2.of(id_str, mergedJson);
                        } catch (JSONException e) {
                            return null;
                        }
                    }
                });


        stream.addSink(new RedisClusterUtils.RedisClusterSink(600));


        // 输出到控制台
        stream.print();

        // 执行任务
        env.execute("Kafka to Redis");
    }
}
