package DanmakuTools;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class EmotionAnalysis {
    public static void main(String[] args) {
        String url = "http://localhost:5000/";
        String text = "{\"type\": \"DANMU_MSG\", \"timestamp\": 1684976117, \"time\": \"2023-05-25 08:55:17\", \"uid\": 669823071, \"color\": 16777215, \"dm_type\": 1, \"font_size\": 25, \"content\": \"开心\", \"recommend_score\": 2}";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            StringEntity params = new StringEntity("text=" + text,"UTF-8");
            post.setEntity(params);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            try (CloseableHttpResponse response = client.execute(post)) {
                String result = EntityUtils.toString(response.getEntity(),"UTF-8");


                System.out.println(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String emo(String text) {
        String url = "http://localhost:5000/";
        // 创建HttpPost对象
        HttpPost post = new HttpPost(url);
        try (CloseableHttpClient client = HttpClients.createDefault()) {

            // 设置请求参数
            StringEntity params = new StringEntity("text=" + text,"UTF-8");
//            System.out.println(text);
            post.setEntity(params);
            // 设置请求头
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            try (CloseableHttpResponse response = client.execute(post)) {
                // 获取响应结果
//                String re = EntityUtils.toString(response.getEntity());
//                System.out.println(re);
                return EntityUtils.toString(response.getEntity(),"UTF-8");
//                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
