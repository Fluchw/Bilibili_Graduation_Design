package DanmakuUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DanmakuConfig {
    public static String KAFKA_BOOTSTRAP_SERVERS;
    public static String REDIS_URI_1;
    public static String REDIS_URI_2;
    public static String REDIS_URI_3;

    public static String ES_HOST;
    public static int ES_PORT;



    static {
        Properties properties = new Properties();
        try (InputStream inputStream = DanmakuConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(inputStream);
            KAFKA_BOOTSTRAP_SERVERS = properties.getProperty("kafka.bootstrap-servers");
            REDIS_URI_1=properties.getProperty("redisURI1");
            REDIS_URI_2=properties.getProperty("redisURI2");
            REDIS_URI_3=properties.getProperty("redisURI3");
            ES_HOST= properties.getProperty("es.host");
            ES_PORT= Integer.parseInt(properties.getProperty("es.port"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
