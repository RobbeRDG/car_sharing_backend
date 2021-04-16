#!/usr/bin/env python
import pika, os, json

# connection setup
URL="amqps://iyopjwwd:ntVEoqTWlN38mXwCrhgdHUmDaOwB86N1@rat.rmq2.cloudamqp.com/iyopjwwd"
params = pika.URLParameters(URL)
connection = pika.BlockingConnection(params)
channel = connection.channel() # start a channel
channel.exchange_declare(exchange='serverToCars', exchange_type='direct', durable=True)
channel.queue_declare(queue='CarRideRequest.3', durable=True, arguments={'x-message-ttl' : 30000})


def on_request(ch, method, props, body):
    print(body)
    print(props.reply_to)
    print(props.correlation_id)

    jsonResponse={
        "messageType": "CarAcknowledgement",
        "carId": 3,
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
channel.basic_consume(queue='CarRideRequest.3', on_message_callback=on_request)

channel.start_consuming()