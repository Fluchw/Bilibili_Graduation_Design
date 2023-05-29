from cnsenti import Emotion
from flask import Flask, request, redirect, url_for

app = Flask(__name__)


@app.route('/', methods=['GET', 'POST'])
def main_page():
    if request.method == 'POST':
        text = request.form['text']
        print(text)
        emotion = Emotion()
        result = emotion.emotion_count(text)
        print(result)
        return redirect(url_for('main_page'))
    # return '''
    #     <form method="post">
    #         输入内容: <input type="text" name="text">
    #         <input type="submit" value="Submit">
    #     </form>
    # '''


if __name__ == '__main__':
    app.run()
