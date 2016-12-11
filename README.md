# Network anti-virus service
[![Build Status](https://travis-ci.org/dvoraka/av-service.svg?branch=master)](https://travis-ci.org/dvoraka/av-service)
[![codecov.io](https://codecov.io/github/dvoraka/av-service/coverage.svg)](https://codecov.io/github/dvoraka/av-service/branch/master)
[![Latest release](https://img.shields.io/badge/release-0.4-brightgreen.svg)](https://github.com/dvoraka/av-service/releases/tag/v0.4)

Replacement for [amqpav](https://github.com/dvoraka/amqpav). Network anti-virus service supports
JMS, AMQP and HTTP for communication. AV-checker project was integrated as the checker submodule
and then completely rewritten and the old code was removed.

 * [Features](#features)
 * [Planned features](#planned-features)
 * [Used components](#used-components)
 * [How to send a message](#how-to-send-a-message-for-check)
 * [Installation](#installation)
 * [Run services](#run-service)
 * [AMQP checker](#amqp-checker)
 * [JMS checker](#jms-checker)
 * [Legacy checker](#old-checker)
 * [Load tests](#load-tests)

--

### Features
 * AMQP 0.9.1 support
 * JMS support
 * REST support
 * Message tracking DB service (PostgreSQL or Solr)
 * Highly scalable architecture
 * Robust design
 
### Planned features
 * Separate REST app for better scaling and load balancer
 * Statistics module
 * AMQP 1.0

### Used components
 * **ClamAV** - open source anti-virus engine
 * **RabbitMQ** - open source message broker for AMQP
 * **ActiveMQ** - open source message broker for JMS
 * **PostgreSQL** - open source DB
 * **Solr** - open source enterprise search platform

**Frameworks and libraries**
 * **Spring**
 * **Spring REST** - REST
 * **Spring Security** - REST security
 * **Spring AMQP** - AMQP
 * **Spring JMS** - JMS
 * **Spring Data** - message logging
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

### Spring profiles
Application is configurable and it's possible to run many configurations. Here is a list
with all profiles and descriptions.

profile | description
---|---
amqp | enable AMQP infrastructure
amqp2jms | bridging AMQP to JMS
amqp-checker | AMQP checker
amqp-rest | AMQP infrastructure for REST
amqp-server | AMQP server
core | core functionality
db | use SQL DB for message logging
db-solr | use Solr for message logging
jms | enable JMS infrastructure
jms2amqp | bridging JMS to AMQP
jms-checker | JMS checker
jms-server | JMS server
no-db | disable message logging
rest | enable REST
rest-amqp | REST to AMQP
rest-client | REST client
rest-local | REST with direct connection

Here will be the best combinations for various usages soon.


### How to send a message for check
You can use AMQP checker from this project for first steps. For sending simple message
you need only:

```java
    public static void main(String[] args) {
    
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("amqp", "amqp-checker", "no-db");
        context.register(AmqpConfig.class);
        context.refresh();

        Checker checker = context.getBean(Checker.class);

        AvMessage message = Utils.genNormalMessage();
        // send message to check exchange
        checker.sendMessage(message);

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
* Solr
* Kibana
* Elasticsearch
* Logstash
* SonarQube

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
$ ./gradlew -q runAmqpCheck
```
And output:
```
Checking... OK
```
or
```
Checking... failed!
```

### JMS checker
Utility for testing JMS infrastructure.

#### Run
```
$ ./gradlew -q runJmsCheck
```
And output:
```
Checking... OK
```
or
```
Checking... failed!
```

### Old checker
You can find old checker under **legacy-checker** release.

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

## Load tests
Various performance testing:
[TESTS](https://github.com/dvoraka/av-service/blob/master/performance.md)
