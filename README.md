# Anti-virus service
[![Build Status](https://travis-ci.org/dvoraka/av-service.svg?branch=master)](https://travis-ci.org/dvoraka/av-service)

Replacement for [amqpav](https://github.com/dvoraka/amqpav).

## Planned features
* Highly scalable architecture
* Robust design
* Communication over AMQP 0.9.1, JMS and REST

**Later:**
* AMQP 1.0
* Message tracking DB service (usefull for bigger deployments)

## Used components
 * **ClamaAV** - open source anti-virus engine
 * **RabbitMQ** - message broker for AMQP
 * **ActiveMQ** - message broker for JMS
 * **Spring REST** - REST

## Installation
### ClamAV
#### Debian
```
# apt-get install clamav-daemon
```
Official [installation](http://www.clamav.net/documents/installing-clamav) for other systems.

##### Configuration
For Jessie it is better to use `dpkg-reconfigure clamav-daemon` and enable TCP socket there because of systemd integration. Don't use names (e.g., **localhost**) as the address.

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
##### Configuration
You can use the script `tools/prepareRMQ.sh` to create the basic configuration. It creates a new virtual host called **antivirus** and adds permissions for **guest** user.

## Run service
Currently everything is rather in a prototype phase.

### AMQP
```
$ ./gradlew runAmqpServer
```
### REST
```
$ ./gradlew appStart
```

### AMQP checker

Utility for testing AMQP infrastructure.
#### Run
```
./gradlew amqpCheck
```
And output should be:
```
...
Test OK

BUILD SUCCESSFUL

Total time: 3.6 secs
```
#### Create jar with dependencies
```
./gradlew :checker:shadowJar
```
And the full jar will be in `checker/build/libs/` directory.

#### Run jar
Built jar is executable.
##### Properties:
* host - message broker host
* dirty - virus flag
* appid - application ID string
```
$ java -Dhost=localhost -jar checker-0.1-SNAPSHOT-all.jar
```
And output should be:
```
...
Test OK
```

## Load results for the old service (retested February 20, 2016):

Message broker, server and anti-virus program are on the same machine. Sending file is EICAR.

Client is on a different machine.

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
