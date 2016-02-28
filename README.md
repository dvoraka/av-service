# av-service

Replacement for [amqpav](https://github.com/dvoraka/amqpav).

#### Load results for the old service (retested):

Message broker, server and anti-virus program are on the same machine. Sending file is EICAR.

Client is on different machine.

* Receiver: AVReceiver
* Sender: AVSender

##### Load tests
```
Load test start for 1000 messages...
Load test end
Duration: 20 s
Messages: 50/s
```
```
Load test start for 10 000 messages...
Load test end
Duration: 178 s
Messages: 56/s
```
```
Load test start for 100 000 messages...
Load test end
Duration: 1800 s
Messages: 56/s
```

## Planned features
* Highly scalable architecture
* Robust design
* Communication over AMQP 0.9.1, JMS and REST

## Installation
### ClamAV
#### Debian
```
# apt-get install clamav
```
Official [installation](http://www.clamav.net/documents/installing-clamav) for other systems.

### RabbitMQ
#### Debian
```
# apt-get install rabbitmq-server
```
