#!/usr/bin/env python
import pika, os, json

# connection setup
URL="amqps://iyopjwwd:ntVEoqTWlN38mXwCrhgdHUmDaOwB86N1@rat.rmq2.cloudamqp.com/iyopjwwd"
params = pika.URLParameters(URL)
connection = pika.BlockingConnection(params)
channel = connection.channel() # start a channel
channel.exchange_declare(exchange='serverToCars', exchange_type='topic', durable=True)
channel.queue_declare(queue='CarRideRequest.2', durable=True, arguments={'x-message-ttl' : 10000})


def on_request(ch, method, props, body):
    print(body)
    print(props.reply_to)
    print(props.correlation_id)

    jsonResponse={
        "messageType": "CarAcknowledgement",
        "carId": 2,
        "rideId": 1,
        "confirmAcknowledge": True,
        "errorMessage": ""
    }

    stateUpdate=json.dumps(jsonResponse)

    channel.basic_publish(exchange='serverToCars',
                        routing_key=props.reply_to,
                        properties=pika.BasicProperties(correlation_id=props.correlation_id),
                        body=stateUpdate)
    ch.basic_ack(delivery_tag=method.delivery_tag)

    print("sent resp")


channel.basic_qos(prefetch_count=1)
channel.basic_consume(queue='CarRideRequest.2', on_message_callback=on_request)

channel.start_consuming()