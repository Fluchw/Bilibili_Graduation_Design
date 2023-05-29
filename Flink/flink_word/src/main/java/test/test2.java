//package bilibili;
//
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.table.api.EnvironmentSettings;
//import org.apache.flink.table.api.TableResult;
//import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
//
//public class test2 {
//    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//    EnvironmentSettings environmentSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
//    StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, environmentSettings);
//
//    String ddl = "create table sink_redis(username VARCHAR, passport VARCHAR) with ( 'connector'='redis', " +
//            "'cluster-nodes'='10.11.80.147:7000,10.11.80.147:7001','redis- mode'='cluster','command'='set')";
//
//    tEnv.executeSql(ddl);
//    String sql = " insert into sink_redis select * from (values ('red', 'test11'))";
//    TableResult tableResult = tEnv.executeSql(sql);
//    tableResult.getJobClient().get().getJobExecutionResult().get();
//
//}
