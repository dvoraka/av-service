# Network file service with anti-virus checking
[![Build Status](https://travis-ci.org/dvoraka/av-service.svg?branch=master)](https://travis-ci.org/dvoraka/av-service)
[![codecov.io](https://codecov.io/github/dvoraka/av-service/coverage.svg)](https://codecov.io/github/dvoraka/av-service/branch/master)
[![Latest release](https://img.shields.io/badge/release-0.10-brightgreen.svg)](https://github.com/dvoraka/av-service/releases/tag/v0.10)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/2e3856220448493482f6de90a0d84ee0)](https://www.codacy.com/app/dvoraka/av-service?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dvoraka/av-service&amp;utm_campaign=Badge_Grade)
[![Open Hub](https://www.openhub.net/p/av-service/widgets/project_thin_badge?format=gif)](https://www.openhub.net/p/av-service/)
[![IntelliJ IDEA](/imgs/idea_logo.png)](https://www.jetbrains.com)

A file service prototype with a replication and anti-virus checking. It is mostly a study project
but it is possible to use it after some "production" tuning.

A replacement for [amqpav](https://github.com/dvoraka/amqpav) and first requirements were from
the old service.
Actual network file service with anti-virus checking supports JMS, AMQP, Kafka and REST for
transport.
AV-checker project was integrated as the checker submodule, then completely rewritten and
the old code was removed.

From version 0.6 it is mostly a file service with an AV checking.
Currently it is still a bit experimental feature.
So if you need the AV checking only you can use the maintained 0.5 version.

### Contribution
Possibilities: Quality assurance (QA), Front-end for REST services (statistics and file storage),
Linux admin for better deployment and distribution, Wiki mage, Server for testing, or we
can find something else for you

---

 * [Release notes](#release-notes)
 * [Features](#features)
 * [Planned features](#planned-features)
 * [Overview](#overview)
 * [Used components](#used-components)
 * [How to send a message](#how-to-send-a-message-for-anti-virus-check)
 * [Installation](#installation)
 * [Run services](#run-service)
 * [AMQP checker](#amqp-checker)
 * [JMS checker](#jms-checker)
 * [Legacy checker](#old-checker)
 * [Load tests](#load-tests)

---

### Release notes
Release [NOTES](RELEASE_NOTES.md)

### Features
 * AMQP 0.9.1 support
 * JMS support
 * Kafka support
 * REST support
 * Message tracking DB service (PostgreSQL or Solr)
 * Statistics module
 * File service with network replication
 * Highly scalable architecture
 * Robust design
 
### Planned features
 * Separate REST app for better scaling
 * REST load balancer
 * AMQP 1.0 support

### Overview
##### Remote REST configuration with file replication
![Remote REST component diagram](/docs/remoterestcomponent.png)

##### Broker connection configuration with file replication
![Broker connection component diagram](/docs/brokercomponent.png)

##### Local REST connection configuration
![Local REST component diagram](/docs/localrestcomponent.png)

### Used components
 * **ClamAV** - open source anti-virus engine
 * **RabbitMQ** - open source message broker used for AMQP
 * **ActiveMQ** - open source message broker used for JMS
 * **Kafka** - open source message broker used for Kafka
 * **PostgreSQL** - open source DB
 * **Solr** - open source enterprise search platform

**Frameworks and libraries**
 * **Spring**
 * **Spring Boot**
 * **Spring REST** - REST
 * **Spring Security** - REST security
 * **Spring AMQP** - AMQP
 * **Spring JMS** - JMS
 * **Spring Kafka** - Kafka
 * **Spring Data** - message logging and file saving
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
 * **Elastic Stack** (Elasticsearch, Logstash, Kibana) - logs analyzing

### Spring profiles
Application is configurable and it's possible to run many configurations. Here is a list
with all profiles and descriptions.

profile | description | from version | to version
---|---|---|---
amqp | enable AMQP infrastructure | |
amqp2jms | bridging AMQP to JMS | | 0.6
amqp-checker | AMQP checker | | 0.6
amqp-file-server | AMQP file server | 0.6 | 0.6
amqp-rest | AMQP infrastructure for REST | 0.5 | 0.6
amqp-server | AMQP server | | 0.6
bridge | enable bridge | 0.7 |
check | enable AV checking | 0.7 |
checker | enable checker | 0.7 |
client | enable client infrastructure | 0.7 |
core | core functionality
db | use SQL DB for message logging
db-mem | use in-memory SQL DB for message logging | 0.8
db-solr | use Solr for message logging
file-client | enable file client | 0.7 |
itest | profile for integration testing | 0.7 |
jms | enable JMS infrastructure
jms2amqp | bridging JMS to AMQP | | 0.6
jms-checker | JMS checker | | 0.6
jms-rest | JMS infrastructure for REST | 0.5 | 0.6
jms-server | JMS server | | 0.6
ldap | enable LDAP infrastructure | 0.7 |
no-db | disable message logging
performance | profile for performance testing | 0.8
replication | enable replication infrastructure | 0.7 |
replication-test | enable replication testing infrastructure | 0.7 |
rest | enable REST
rest-amqp | REST to AMQP | 0.5
rest-jms | REST to JMS | 0.5
rest-local | REST with direct connection | 0.5
server | enable server infrastructure | 0.7 |
stats | enable statistics | 0.5
stats-solr | statistics in Solr | 0.5
storage | enable file service | 0.6
storage-check | profile for storage testing | 0.8
to-amqp | bridging to AMQP | 0.7 |
to-jms | bridging to JMS | 0.7 |

Here will be the best combinations for various usages soon.

You can find all profiles for a concrete code base with the script **findAllSpringProfiles.sh**
in the ```tools``` directory.

### How to send a message for anti-virus check

AvCheckExample class:
```java
package dvoraka.avservice.client.example;

import dvoraka.avservice.client.AvMessageFuture;
import dvoraka.avservice.client.configuration.ClientConfig;
import dvoraka.avservice.client.service.AvServiceClient;
import dvoraka.avservice.common.util.Utils;
import dvoraka.avservice.common.data.AvMessage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Anti-virus checking example.
 */
public final class AvCheckExample {

    public static void main(String[] args) throws InterruptedException {
        // initialize client context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("client", "amqp", "file-client", "no-db");
        context.register(ClientConfig.class);
        context.refresh();

        // get client
        AvServiceClient avServiceClient = context.getBean(AvServiceClient.class);

        // generate message
        AvMessage avMessage = Utils.genMessage();

        // send it and get response
        AvMessage response;
        try {
            AvMessageFuture futureResponse = avServiceClient.checkMessage(avMessage);
            response = futureResponse.get();
        } finally {
            context.close();
        }

        // raw output
        System.out.println("Response: " + response);
        // virus info
        System.out.println("Virus info: " + (response != null ? response.getVirusInfo() : ""));
    }
}

```

### Installation
You can use Docker to prepare necessary services for a development.
The Docker configurations are a good documentation for installing services without Docker.
Everything is inside the ```docker/``` directory.

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
* 2 app replication nodes
* SonarQube (optional)

Change your working directory to ```docker/```:
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

### Run service
You can run all services easily with Gradle.

#### AMQP
Anti-virus checking server:
```
$ ./gradlew runAmqpServer
```
File server with anti-virus checking:
```
$ ./gradlew runAmqpFileServer
```
#### JMS
Anti-virus checking server:
```
$ ./gradlew runJmsServer
```
File server with anti-virus checking:
```
$ ./gradlew runJmsFileServer
```
#### REST
It is a Spring Boot application. You can run it with Gradle or use an executable jar.

**Gradle**
```
$ ./gradlew rest:bootRun
```
**Spring Boot**

You need to build an executable jar:
```
$ ./gradlew assemble
```
And then the the jar is in `rest/build/libs/` directory called **avservice-rest-XXX.jar**.
You can run it with:
```
$ java -jar avservice-rest-0.9.jar 

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::            (v1.5.4.RELEASE)
```

### AMQP checker
Utility for testing AMQP infrastructure. It sends a message and wait for a response.

#### Run
```
$ ./gradlew -q runAmqpCheck
```

### JMS checker
Utility for testing JMS infrastructure. It sends a message and wait for a response.

#### Run
```
$ ./gradlew -q runJmsCheck
```

### Check output logs

```
12:20:00.388 [main] INFO  d.avservice.client.checker.CheckApp - Checking... 
12:20:03.807 [main] INFO  d.avservice.client.checker.CheckApp - failed!
12:20:03.808 [main] INFO  d.avservice.client.checker.CheckApp - Check completed.
```
or
```
12:20:00.388 [main] INFO  d.avservice.client.checker.CheckApp - Checking... 
12:20:03.807 [main] INFO  d.avservice.client.checker.CheckApp - OK
12:20:03.808 [main] INFO  d.avservice.client.checker.CheckApp - Check completed.
```

---

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
