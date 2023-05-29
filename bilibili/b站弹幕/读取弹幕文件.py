import json
import pprint

with open('bilibili-api-main/danmaku.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

# pprint.pprint(data)
print(len(data))