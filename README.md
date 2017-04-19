# Network anti-virus service
[![Build Status](https://travis-ci.org/dvoraka/av-service.svg?branch=master)](https://travis-ci.org/dvoraka/av-service)
[![codecov.io](https://codecov.io/github/dvoraka/av-service/coverage.svg)](https://codecov.io/github/dvoraka/av-service/branch/master)
[![Latest release](https://img.shields.io/badge/release-0.6-brightgreen.svg)](https://github.com/dvoraka/av-service/releases/tag/v0.6)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/2e3856220448493482f6de90a0d84ee0)](https://www.codacy.com/app/dvoraka/av-service?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dvoraka/av-service&amp;utm_campaign=Badge_Grade)
[![Open Hub](https://www.openhub.net/p/av-service/widgets/project_thin_badge?format=gif)](https://www.openhub.net/p/av-service/)

A replacement for [amqpav](https://github.com/dvoraka/amqpav)
and first requirements were from the old service.
Actual network anti-virus service supports JMS, AMQP and REST for communication.
AV-checker project was integrated as the checker submodule,
then completely rewritten and the old code was removed.

From version 0.6 it is mostly a file service with an AV checking.
Currently it is still a bit experimental feature.

### Contribution
Possibilities: Quality assurance (QA), Front-end for REST services (statistics and file storage),
Linux admin for better deployment and distribution, Server for testing

--

 * [Release notes](#release-notes)
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

### Release notes
Release [NOTES](RELEASE_NOTES.md)

### Features
 * AMQP 0.9.1 support
 * JMS support
 * REST support
 * Message tracking DB service (PostgreSQL or Solr)
 * Statistics module
 * File service
 * Highly scalable architecture
 * Robust design
 
### Planned features
 * Separate REST app for better scaling and load balancer
 * AMQP 1.0

### Used components
 * **ClamAV** - open source anti-virus engine
 * **RabbitMQ** - open source message broker used for AMQP
 * **ActiveMQ** - open source message broker used for JMS
 * **PostgreSQL** - open source DB
 * **Solr** - open source enterprise search platform

**Frameworks and libraries**
 * **Spring**
 * **Spring Boot**
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

profile | description | from version
---|---|---
amqp | enable AMQP infrastructure
amqp2jms | bridging AMQP to JMS
amqp-checker | AMQP checker
amqp-file-server | AMQP file server | 0.6
amqp-rest | AMQP infrastructure for REST | 0.5
amqp-server | AMQP server
core | core functionality
db | use SQL DB for message logging
db-solr | use Solr for message logging
jms | enable JMS infrastructure
jms2amqp | bridging JMS to AMQP
jms-checker | JMS checker
jms-rest | JMS infrastructure for REST | 0.5
jms-server | JMS server
no-db | disable message logging
rest | enable REST
rest-amqp | REST to AMQP | 0.5
rest-jms | REST to JMS | 0.5
rest-local | REST with direct connection | 0.5
stats | enable statistics | 0.5
stats-solr | statistics in Solr | 0.5
storage | enable file service | 0.6

Here will be the best combinations for various usages soon.

You can find all profiles for a concrete code base with the script **findAllSpringProfiles.sh**
in the ```tools``` directory.

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

        AvMessage message = Utils.genMessage();
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
It is now a Spring Boot application. You can run it with Gradle or use an executable jar.

**Gradle**
```
$ ./gradlew rest:bootRun
```
**Spring Boot**

You need to build the executable jar:
```
$ ./gradlew assemble
```
And then the the jar is in `rest/build/libs/` directory called **avservice-rest-XXX.jar**.
You can run it with:
```
$ java -jar avservice-rest-0.5.jar 

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::            (v1.5.0.RC1)
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

--

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
[TESTS](performance.md)
