import json

from cnsenti import Emotion
from flask import Flask, request, Response

app = Flask(__name__)


@app.route('/', methods=['POST'])
def main_page():

    text = request.form['text']
    # print(text)
    data_json = json.loads(text)
    # print(emo_json)
    # print(type(text))
    emotion = Emotion()
    emo_json = {'emo':emotion.emotion_count(data_json['content'])}
    # print(result)

    data_json.update(emo_json)
    # emo = {
    #
    #     'emo': {
    #         '好': result['好'],
    #         '乐': result['乐'],
    #         '哀': result['哀'],
    #         '怒': result['怒'],
    #         '惧': result['惧'],
    #         '恶': result['恶'],
    #         '惊': result['惊']
    #     }
    # }

    # response_text = str(emo)
    print(data_json)
    response_text=str(data_json)

    return Response(response_text, mimetype='text/plain')


if __name__ == '__main__':
    app.run()
