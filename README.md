# av-service

Replacement for [amqpav](https://github.com/dvoraka/amqpav).

#### Load results for the old service (retested):

Load tests with debug output on server. Client, message broker, server and anti-virus program are on the same machine. Sending file is EICAR.

* Receiver: AVReceiver
* Sender: AVSender

##### Sending only
```
Load test start for 1000 messages...
Load test end
Duration: 13 s
```
```
Load test start for 10000 messages...
Load test end
Duration: 98 s
```
```
Load test start for 100000 messages...
Load test end
Duration: 895 s
```
##### Synchronous variant
```
Load test start for 1000 messages...
Load test end
Duration: 19 s
```
```
Load test start for 10000 messages...
Load test end
Duration: 156 s
```
```
Load test start for 100000 messages...
Load test end
Duration: 1903 s
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
