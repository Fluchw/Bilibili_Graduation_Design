package DanmakuUtils;

import java.util.HashMap;
import java.util.Map;

//将10进制颜色代码转换为中文
public class ColorConverterUtils {
    private static final Map<String, String> colors = new HashMap<>();

    static {
        colors.put("FFFFFF", "白色");
        colors.put("E33FFF", "紫色");
        colors.put("54EED8", "松石绿");
        colors.put("58C1DE", "雨后蓝");
        colors.put("455FF6", "星空蓝");
        colors.put("975EF9", "紫罗兰");
        colors.put("C35986", "梦境红");
        colors.put("FF8C21", "热力橙");
        colors.put("FFF522", "香槟金");
        colors.put("FF6868", "红色");
        colors.put("66CCFF", "蓝色");
        colors.put("FFD700", "盛典金");
        colors.put("4169E1", "升腾蓝");
        colors.put("00FFFC", "青色");
        colors.put("7EFF00", "绿色");
        colors.put("FFED4F", "黄色");
        colors.put("FF9800", "橙色");
        colors.put("FF739A", "粉色");
    }

    public static String color2cn(int color10) {
        String color16 = Integer.toHexString(color10).toUpperCase();
        String color = colors.getOrDefault(color16, "其他");

        return color;
    }
}
