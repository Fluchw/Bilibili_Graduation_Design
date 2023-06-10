package DanmakuUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huaban.analysis.jieba.JiebaSegmenter;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EmotionUtils {
    private static Set<String> Haos;
    private static Set<String> Les;
    private static Set<String> Ais;
    private static Set<String> Nus;
    private static Set<String> Jus;
    private static Set<String> Wus;
    private static Set<String> Jings;
    private static Set<String> stopWords;

//    读取词典
    static {
        try {
            Haos = new HashSet<>(JSON.parseObject(IOUtils.toString(EmotionUtils.class.getResourceAsStream("/senti/好.json"), StandardCharsets.UTF_8), new TypeReference<List<String>>() {
            }));
            Les = new HashSet<>(JSON.parseObject(IOUtils.toString(EmotionUtils.class.getResourceAsStream("/senti/乐.json"), StandardCharsets.UTF_8), new TypeReference<List<String>>() {
            }));
            Ais = new HashSet<>(JSON.parseObject(IOUtils.toString(EmotionUtils.class.getResourceAsStream("/senti/哀.json"), StandardCharsets.UTF_8), new TypeReference<List<String>>() {
            }));
            Nus = new HashSet<>(JSON.parseObject(IOUtils.toString(EmotionUtils.class.getResourceAsStream("/senti/怒.json"), StandardCharsets.UTF_8), new TypeReference<List<String>>() {
            }));
            Jus = new HashSet<>(JSON.parseObject(IOUtils.toString(EmotionUtils.class.getResourceAsStream("/senti/惧.json"), StandardCharsets.UTF_8), new TypeReference<List<String>>() {
            }));
            Wus = new HashSet<>(JSON.parseObject(IOUtils.toString(EmotionUtils.class.getResourceAsStream("/senti/恶.json"), StandardCharsets.UTF_8), new TypeReference<List<String>>() {
            }));
            Jings = new HashSet<>(JSON.parseObject(IOUtils.toString(EmotionUtils.class.getResourceAsStream("/senti/惊.json"), StandardCharsets.UTF_8), new TypeReference<List<String>>() {
            }));

            // 读取停用词表文件
            stopWords = IOUtils.readLines(EmotionUtils.class.getResourceAsStream("/senti/StopWords.txt"), StandardCharsets.UTF_8).stream().collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException("Error initializing EmotionUtils class", e);
        }

    }


    public static String emotionCount(String text) throws IOException {
        int sentences = Pattern.compile("[.。！!？?\\n;；]+").split(text).length;

        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<String> words = segmenter.sentenceProcess(text);
        int wordnum = words.size();

        // 去除停用词
        words = words.stream().filter(word -> !stopWords.contains(word)).collect(Collectors.toList());

        Map<String, Long> result = words.stream()
                .filter(word -> Haos.contains(word) || Les.contains(word) || Ais.contains(word) || Nus.contains(word) || Jus.contains(word) || Wus.contains(word) || Jings.contains(word))
                .collect(Collectors.groupingBy(word -> {
                    if (Haos.contains(word)) return "好";
                    if (Les.contains(word)) return "乐";
                    if (Ais.contains(word)) return "哀";
                    if (Nus.contains(word)) return "怒";
                    if (Jus.contains(word)) return "惧";
                    if (Wus.contains(word)) return "恶";
                    if (Jings.contains(word)) return "惊";
                    return "";
                }, Collectors.counting()));

        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("sentences", sentences);
        finalResult.put("words", wordnum);
        finalResult.put("好", result.getOrDefault("好", 0L));
        finalResult.put("乐", result.getOrDefault("乐", 0L));
        finalResult.put("哀", result.getOrDefault("哀", 0L));
        finalResult.put("怒", result.getOrDefault("怒", 0L));
        finalResult.put("惧", result.getOrDefault("惧", 0L));
        finalResult.put("恶", result.getOrDefault("恶", 0L));
        finalResult.put("惊", result.getOrDefault("惊", 0L));

        try {
            return JSON.toJSONString(finalResult);
        } catch (Exception e) {
            throw new IOException("Error converting result to JSON string", e);
        }
    }
}
