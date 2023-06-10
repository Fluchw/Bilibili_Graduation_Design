package DanmakuUtils;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch7.ElasticsearchSink;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.util.ArrayList;
import java.util.List;

public class EsUtils {
//    public static void main(String[] args) throws Exception {
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//
//        // 从外部数据源读取数据
//        DataStream<String> dataStream = env.readTextFile("C:\\Users\\wuss\\Desktop\\xuexi\\Scala\\Flink\\flink_word\\data\\test.json");
//
//
//        // 设置Elasticsearch连接信息
////        List<HttpHost> httpHosts = new ArrayList<>();
////        httpHosts.add(new HttpHost("hadoop102", 9200, "http"));
//
//        // 创建ElasticsearchSink
////        ElasticsearchSink.Builder<String> esSinkBuilder = createElasticsearchSink();
//
//        // 将ElasticsearchSink添加到数据流中
////        dataStream.addSink(esSinkBuilder.build());
//        DataStream<Tuple2<String, String>> outputstream = dataStream
//                .map(new MapFunction<String, Tuple2<String, String>>() {
//                    @Override
//                    public Tuple2<String, String> map(String value) throws Exception {
//                        JSONObject json = JSON.parseObject(value);
//                        String id_str = json.getString("id_str");
//                        return Tuple2.of(id_str, value);
//                    }
//                });
//
//        DataStream<Tuple3<String, String, JSONObject>> stream = outputstream
//                .map(new MapFunction<Tuple2<String, String>, Tuple3<String, String, JSONObject>>() {
//                    @Override
//                    public Tuple3<String, String, JSONObject> map(Tuple2<String, String> value) throws Exception {
//                        String data = value.f1;
//                        JSONObject json = JSON.parseObject(data);
//                        String indexName = json.getString("type");
//                        String documentId = json.getString("id_str");
//                        return Tuple3.of(indexName, documentId, json);
//                    }
//                });
//
//
//        // 创建ElasticsearchSink
//        ElasticsearchSink.Builder<Tuple3<String, String, JSONObject>> esSinkBuilder = createElasticsearchSink();
//
//        // 将ElasticsearchSink添加到数据流中
//        stream.addSink(esSinkBuilder.build());
//
//        // 输出到控制台
//        stream.print();
//
//        env.execute("Flink to Elasticsearch Example");
//    }

    public static ElasticsearchSink.Builder<Tuple3<String, String, JSONObject>> createElasticsearchSink() {
        // 设置Elasticsearch连接信息
        List<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost(DanmakuUtils.DanmakuConfig.ES_HOST, DanmakuUtils.DanmakuConfig.ES_PORT, "http"));

        ElasticsearchSink.Builder<Tuple3<String, String, JSONObject>> esSinkBuilder = new ElasticsearchSink.Builder<>(
                httpHosts,
                new ElasticsearchSinkFunction<Tuple3<String, String, JSONObject>>() {
                    // 创建索引请求
                    public IndexRequest createIndexRequest(Tuple3<String, String, JSONObject> element) {
                        // 将字符串转换为JSON对象
                        String ind = element.f0;

                        String indexName = ind.toLowerCase();
                        String documentId = element.f1;
                        JSONObject json = element.f2;

                        return Requests.indexRequest()
                                //表名
                                .index(indexName)
                                .id(documentId)
                                .source(json);


                    }


                    @Override
                    public void process(Tuple3<String, String, JSONObject> element, RuntimeContext ctx, RequestIndexer indexer) {
                        indexer.add(createIndexRequest(element));
                    }
                }
        );

        // 设置批量写入的缓冲区大小
        esSinkBuilder.setBulkFlushMaxActions(1);

        return esSinkBuilder;
    }
}
