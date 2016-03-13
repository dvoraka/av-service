# av-service

Replacement for [amqpav](https://github.com/dvoraka/amqpav).

## Planned features
* Highly scalable architecture
* Robust design
* Communication over AMQP 0.9.1, JMS and REST

## Installation
### ClamAV
#### Debian
```
# apt-get install clamav-daemon
```
Official [installation](http://www.clamav.net/documents/installing-clamav) for other systems.

##### Configuration
For Jessie it is better to use `dpkg-reconfigure clamav-daemon` and enable TCP socket there because of systemd integration.

OR

Manually change the configuration file `/etc/clamav/clamd.conf`:
```
TCPSocket 3310
TCPAddr 127.0.0.1
```
This is for enabling TCP socket on a default port and localhost.

### RabbitMQ
#### Debian
```
# apt-get install rabbitmq-server
```

## Load results for the old service (retested):

Message broker, server and anti-virus program are on the same machine. Sending file is EICAR.

Client is on different machine.

* Receiver: AVReceiver
* Sender: AVSender

##### How to run load test
```
$ ./gradlew loadTest
```
Configuration file is loadTest.xml in resources directory.

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
