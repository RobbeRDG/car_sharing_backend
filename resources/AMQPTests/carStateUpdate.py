#!/usr/bin/env python
import pika, os, json

URL="amqps://iyopjwwd:ntVEoqTWlN38mXwCrhgdHUmDaOwB86N1@rat.rmq2.cloudamqp.com/iyopjwwd"
JSON_MESSAGE={
    "carId": 3,
    "createdOn": "2021-04-06T13:36:01.000+00:00",
    "online": True,
    "remainingFuelInKilometers": 314,
    "location": {
        "type": "Point",
        "coordinates": [
            3.57666,
            50.91656
        ]
    }
}
STATE_UPDATE=json.dumps(JSON_MESSAGE)

test = "testString"

# connection setup
params = pika.URLParameters(URL)
connection = pika.BlockingConnection(params)
channel = connection.channel() # start a channel
channel.queue_declare(queue='carStateUpdatesToServer', durable=True)
channel.exchange_declare(exchange='carStateExchangeToServer', exchange_type='direct', durable=True)
channel.basic_publish(exchange='carStateExchangeToServer',
                      routing_key='stateUpdate',
                      body=STATE_UPDATE)

print("sent state update")
connection.close()