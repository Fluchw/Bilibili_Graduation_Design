# -*- coding: utf-8 -*-

import os
import time

import json

# from bilibili_api import Credential
from bilibili_api import video


async def main():
    BVID = "BV1D441157fh"
    # cred = Credential(sessdata="d076751d%2C1699108719%2C726ef%2A52")
    # video_obj = video.Video(BVID, credential=cred)
    video_obj = video.Video(BVID)
    danmakus = await video_obj.get_danmakus()
    data = [dm.to_dict() for dm in danmakus]
    # print(data)
    with open('danmaku.json', 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False)

    # print('ok!')


