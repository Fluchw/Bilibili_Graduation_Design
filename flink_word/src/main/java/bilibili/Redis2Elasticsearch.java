package bilibili;

import DanmakuUtils.RedisClusterUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch7.ElasticsearchSink;

import static DanmakuUtils.EsUtils.createElasticsearchSink;

public class Redis2Elasticsearch {
    public static void main(String[] args) throws Exception {
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 从redis读取数据
        DataStreamSource<Tuple2<String, String>> inputStream = env.addSource(new RedisClusterUtils.RedisClusterSource());
        inputStream.print();

//        注意:可能收到两个warning,修复后可能会出现问题
//        warning:匿名 new MapFunction<Tuple2<String, String>, Tuple3<String, String, JSONObject>>() 可替换为 lambda
//        warning:此方法中从未抛出异常 'java.lang.Exception'

        //        将数据处理成Elasticsearch保存的形式
        DataStream<Tuple3<String, String, JSONObject>> stream = inputStream
                .map(new MapFunction<Tuple2<String, String>, Tuple3<String, String, JSONObject>>() {
                    @Override
                    public Tuple3<String, String, JSONObject> map(Tuple2<String, String> value) throws Exception {
                        String data = value.f1;
                        JSONObject json = JSON.parseObject(data);
//                        String indexName = json.getString("type");
//                        仅测试用
                        String indexName ="msg_test";

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
        env.execute("Redis to Elasticsearch");
    }
}
