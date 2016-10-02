# Anti-virus service
[![Build Status](https://travis-ci.org/dvoraka/av-service.svg?branch=master)](https://travis-ci.org/dvoraka/av-service)
[![codecov.io](https://codecov.io/github/dvoraka/av-service/coverage.svg)](https://codecov.io/github/dvoraka/av-service/branch/master)

Replacement for [amqpav](https://github.com/dvoraka/amqpav). Network anti-virus service supports JMS, AMQP and HTTP for communication. AV-checker project was integrated as checker submodule.

 * [Features](#features)
 * [Planned features](#planned-features)
 * [Used components](#used-components)
 * [How to send a message](#how-to-send-a-message-for-check)
 * [Installation](#installation)
 * [Run services](#run-service)
 * [Load tests](#load-results-for-the-old-service-retested-february-20-2016)

--

### Features
 * AMQP 0.9.1 support
 * JMS support
 * REST support
 * Message tracking DB service (useful for bigger deployments)
 * Highly scalable architecture
 * Robust design
 
### Planned features
 * Separate REST app for better scaling and load balancer
 * Statistics module
 * AMQP 1.0

### Used components
 * **ClamaAV** - open source anti-virus engine
 * **RabbitMQ** - open source message broker for AMQP
 * **ActiveMQ** - open source message broker for JMS
 * **PostgreSQL** - open source DB

**Frameworks and libraries**
 * **Spring**
 * **Spring REST** - REST
 * **Spring Security** - REST security
 * **Spring AMQP** - AMQP
 * **Spring JMS** - JMS
 * **Spring Data JPA** - DB message logging
 * **Ehcache 3** - caching
 * **Spock** - testing
 * **Log4j 2** - logging API
 * **Logback** - logging

**Code quality**
 * **Checkstyle**
 * **FindBugs**
 * **PMD**
 * **JaCoCo**
 * **SonarQube**

**Build**
 * **Gradle** - build
 * **Docker** - environment preparation

**Development**
 * **ELK Stack** (Elasticsearch, Logstash, Kibana) - logs analyzing

### How to send a message for check
You can use AMQP client from this project for first steps. For sending simple message you need only:

```java
    public static void main(String[] args) {
    
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("amqp-client");
        context.register(AmqpClientConfig.class);
        context.refresh();

        AmqpClient client = context.getBean(AmqpClient.class);

        AvMessage message = Utils.genNormalMessage();
        // send message to check exchange
        client.sendMessage(message, "check");

        context.close();
    }
```

### Installation
You can use Docker to prepare necessary services for development.

#### Docker
Docker is recommended approach.

**Services**
* RabbitMQ
* ActiveMQ
* ClamAV daemon
* PostgreSQL
* Kibana
* Elasticsearch
* Logstash

Change your working directory to *docker*:
```
$ cd docker/
```
And run ```docker-compose up``` command:
```
$ docker-compose up
```
It prepares services running on **localhost**.

**Configuration:**

For setting environment it is necessary to run a command from the **root** directory:

change to project root
```
$ cd ..
```
and
```
$ ./gradlew configureEnvironment
```
And everything should be prepared for application running.

### Manual installation (currently not recommended and updated)
#### ClamAV
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

#### RabbitMQ
#### Debian
```
# apt-get install rabbitmq-server
```
##### Configuration
You can use the script `tools/prepareRMQ.sh` to create the basic configuration. It creates a new virtual host called **antivirus** and adds permissions for **guest** user.

### Run service
You can run all services easily with Gradle.

#### AMQP
```
$ ./gradlew runAmqpServer
```
#### JMS
```
$ ./gradlew runJmsServer
```
#### REST
```
$ ./gradlew appStart
```

### AMQP checker

Utility for testing AMQP infrastructure.
#### Run
```
$ ./gradlew -q amqpCheck
```
And output should be:
```
...
Test OK
```
#### Create jar with dependencies
```
$ ./gradlew :checker:shadowJar
```
And the full jar will be in `checker/build/libs/` directory.

#### Run jar
Built jar is executable.
##### Properties:
* host - message broker host
* infected - infection flag (true/false)
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
