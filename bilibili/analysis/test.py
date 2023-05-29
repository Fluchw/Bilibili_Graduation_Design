from cnsenti import Emotion


# 定义情感分析函数
def analyze_sentiment(tuple: tuple):
    dm_time = tuple[0]
    text = tuple[1]
    emotion = Emotion()
    result = emotion.emotion_count(text)
    return dm_time, result


def merge_dicts(dict1: dict, dict2: dict):
    # 创建一个空字典，用来存储结果
    result = {}

    # 获取两个字典中所有键的并集
    keys = set(dict1) | set(dict2)

    # 遍历所有键
    for key in keys:
        # 计算每个键在两个字典中对应的值之和,若对应的键不存在与字典中,则将值设置为0
        result[key] = dict1.get(key, 0) + dict2.get(key, 0)

    # 返回结果字典
    return result


