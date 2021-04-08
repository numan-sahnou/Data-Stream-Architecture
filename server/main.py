from flask_socketio import SocketIO, emit
from flask import Flask, render_template, url_for, copy_current_request_context
from random import random
from time import sleep
from threading import Thread, Event
from kafka import KafkaConsumer
import time
import json

__author__ = 'PeaceWatch'

app = Flask(__name__)
app.config['SECRET_KEY'] = 'secret!'
app.config['DEBUG'] = True

socketio = SocketIO(app, async_mode=None, logger=True, engineio_logger=True)

thread = Thread()
thread_stop_event = Event()


def alertStreaming():
    # To consume latest messages and auto-commit offsets
    consumer = KafkaConsumer('alerts',
                         group_id='my_group2',
                         bootstrap_servers=['localhost:9092'])
    while not thread_stop_event.isSet():
        for message in consumer:
            print ("%s:%d:%d: key=%s value=%s" % (message.topic, message.partition,
                                          message.offset, message.key,
                                          message.value))
            js = json.loads(message.value)
            socketio.emit('alert', {'alerts': js}, namespace='/test')


@app.route('/')
def index():
    #send the page to connect the client
    return render_template('index.html')

@socketio.on('connect', namespace='/test')
def test_connect():
    # need visibility of the global thread object
    global thread
    #Start the streaming thread only if the thread has not been started before.
    if not thread.isAlive():
        thread = socketio.start_background_task(alertStreaming)

@socketio.on('disconnect', namespace='/test')
def test_disconnect():
    print('Client disconnected')


if __name__ == '__main__':
    socketio.run(app)

"""
--server setup--

python3 -m venv venv / py -m venv venv
source venv/bin/activate / venv\Scripts\ activate
pip install flask
pip install kafka-python
pip install flask_socketio
export FLASK_APP=main.py / $env:FLASK_APP = "main.py" (windows)
flask run
http://localhost:5000/index
"""
