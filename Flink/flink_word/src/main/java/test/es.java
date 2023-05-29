package test;

import DanmakuTools.Lettuce;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch7.ElasticsearchSink;

import static DanmakuTools.EsTool.createElasticsearchSink;

public class es {
    public static void main(String[] args) throws Exception {
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> dataStream = env.readTextFile("C:\\Users\\wuss\\Desktop\\xuexi\\Scala\\Flink\\flink_word\\data\\test.json");

        DataStream<Tuple2<String, String>> outputstream = dataStream
                .map(new MapFunction<String, Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> map(String value) throws Exception {
                        JSONObject json = JSON.parseObject(value);
                        String id_str = json.getString("id_str");
//                        System.out.println(id_str);
                        return Tuple2.of(id_str, value);
                    }
                });


        //写入redis
        outputstream.addSink(new Lettuce.RedisClusterSink(600));

        // 从redis读取数据
        DataStreamSource<Tuple2<String, String>> inputStream = env.addSource(new Lettuce.RedisClusterSource());
        inputStream.print();

//        将数据处理成Elasticsearch保存的形式
        DataStream<Tuple3<String, String, JSONObject>> stream = inputStream
                .map(new MapFunction<Tuple2<String, String>, Tuple3<String, String, JSONObject>>() {
                    @Override
                    public Tuple3<String, String, JSONObject> map(Tuple2<String, String> value) throws Exception {
                        String data = value.f1;
                        JSONObject json = JSON.parseObject(data);
                        String indexName = json.getString("type");
                        String documentId = json.getString("id_str");
                        return Tuple3.of(indexName, documentId, json);
                    }
                });


        stream.print();
        // 创建ElasticsearchSink
        ElasticsearchSink.Builder<Tuple3<String, String, JSONObject>> esSinkBuilder = createElasticsearchSink();

        // 将ElasticsearchSink添加到数据流中
        stream.addSink(esSinkBuilder.build());

        // 输出到控制台
//        inputStream.print();

        // 执行任务
        env.execute("Redis");
    }
}
