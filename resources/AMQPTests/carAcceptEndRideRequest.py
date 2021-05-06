#!/usr/bin/env python
import pika, os, json

# connection setup
URL="amqps://iyopjwwd:ntVEoqTWlN38mXwCrhgdHUmDaOwB86N1@rat.rmq2.cloudamqp.com/iyopjwwd"
params = pika.URLParameters(URL)
connection = pika.BlockingConnection(params)
channel = connection.channel() # start a channel
channel.exchange_declare(exchange='serverToCars', exchange_type='topic', durable=True)
channel.queue_declare(queue='CarEndRideRequest.3')


def on_request(ch, method, props, body):
    jsonResponse={
        "messageCreationTimestamp": 
        "messageType": "CarAcknowledgement",
        "carId": 3,
        "rideId": 1,
        "confirmAcknowledge": True,
        "errorMessage": ""
    }

    stateUpdate=json.dumps(jsonResponse)

    channel.basic_publish(exchange='serverToCars',
                        routing_key='external.cars.CarEndRideRequest.3',
                        properties=pika.BasicProperties(correlation_id = \
                                                            props.correlation_id),
                        body=stateUpdate)


channel.basic_qos(prefetch_count=1)
channel.basic_consume(queue='CarEndRideRequest.3', on_message_callback=on_request)

channel.start_consuming()