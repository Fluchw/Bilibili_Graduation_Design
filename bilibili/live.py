import sys
import time

import json
from bilibili_api import live, sync

if len(sys.argv) > 1:
    room_id = int(sys.argv[1])
    room = live.LiveDanmaku(room_id)
else:
    print("请在命令行中输入room_id")


def save_json(file_name, json_data):
    with open(f'/opt/module/danmaku/bilibili-api-main/json/{file_name}.json', 'a', encoding='utf-8') as f:
        f.write(json_data + '\n')


# 收到弹幕
@room.on('DANMU_MSG')
async def on_danmaku(event):

    # 是一个字符串
    extra_str: str = event['data']['info'][0][15]['extra']
    extra_list = extra_str.split(',')
    data_str = ','.join(extra_list)
    extra_data = json.loads(data_str)
    uid = event['data']['info'][2][0]
    ts = event['data']['info'][9]['ts']
    datatime_format = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(ts))
    time_format = time.strftime('%H:%M:%S', time.localtime(ts))


    danmaku_data = {
        "type": event['type'],
        "id_str": extra_data["id_str"],
        'timestamp': ts,
        'datatime_format': datatime_format,
        'time_format': time_format,
        'uid': uid,
        "color": extra_data["color"],
        "dm_type": extra_data["dm_type"],
        "font_size": extra_data["font_size"],
        "content": extra_data["content"],
        "recommend_score": extra_data["recommend_score"],
    }

    json_data = json.dumps(danmaku_data, ensure_ascii=False)
    print(json_data)

    save_json('DANMU_MSG', json_data)


# 礼物
# @room.on('SEND_GIFT')
# async def on_gift(event):
#     data = event['data']['data']
#
#     gift_data = {
#         "type": event["type"],
#         "time": data["timestamp"],
#         "action": data["action"],
#         "giftId": data["giftId"],
#         "giftName": data["giftName"],
#         "num": data["num"],
#         "uid": data["uid"],
#         "uname": data["uname"],
#     }
#
#     json_data = json.dumps(gift_data, ensure_ascii=False)
#     print(json_data)
#     # print(event)
#     save_json('SEND_GIFT',json_data)
#
#
# # 直播间人气更新
# @room.on('VIEW')
# async def on_view(event):
#     timestamp = int(time.time())
#     view_data = {'type': event['type'], 'timestamp': timestamp, 'view_count': event['data']}
#     json_data = json.dumps(view_data)
#     print(json_data)
#     save_json('VIEW',json_data)


# @room.on('ALL')
# async def LIVE_OPEN_PLATFORM_DM(event):
#     print(event)

sync(room.connect())
