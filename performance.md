# Performance notes

All tests until Feb 17, 2017 ran on the old computer.

#### New computer and settings

 * CPU: i5 4.0 GHz (4 cores)
 * Storage: SSD
 * Logging level: WARN
 * Settings: 4 threads, no message logging

#### Old computer and settings

 * CPU: i3 2.40 GHz (2 cores)
 * Storage: SSD
 * Logging level: INFO
 * Settings: 4 threads, no message logging

## Testing server performance

AMQP server run command: `./gradlew runAmqpServer`

AMQP load test run command: `./gradlew runAmqpLoadTest`

JMS server run command: `./gradlew runJmsServer`

JMS load test run command: `./gradlew runJmsLoadTest`

Old load test run command: `./gradlew loadTest`

Message logging: no

### Other scenarios
#### Results on the old computer
 * **1 million** messages with AMQP server. It didn't show any memory leaks.
 * Result: **557** msgs/s
 * **1 million** messages with JMS server. It didn't show any memory leaks.
 * Result: **643** msgs/s
 
---

### Default scenarios

First run is for a warm-up and is not counted.

### AMQP

#### May 14, 2017
With a new BufferedPerformanceTester class.

run | 1. | 2. | 3. | 4. | 5.
---|---|---|---|---|---
msg/s | 1628 | 1545 | 1663 | 1607 | 1552

#### May 2, 2017
With the new computer.

run | 1. | 2. | 3. | 4. | 5.
---|---|---|---|---|---
msg/s | 926 | 907 | 897 | 888 | 913

Socket pooling is default.

#### Results on the old computer

#### Feb 17, 2017
With an experimental ClamAV socket pooling.

run | 1. | 2. | 3. | 4. | 5.
---|---|---|---|---|---
msg/s | 563 | 559 | 560 | 556 | 553

Without the socket pooling.

run | 1. | 2. | 3. | 4. | 5.
---|---|---|---|---|---
msg/s | 528 | 532 | 532 | 517 | 527

#### Jan 16, 2017
Version 0.5-rc1.

run | 1. | 2. | 3. | 4. | 5.
---|---|---|---|---|---
msg/s | 524 | 515 | 515 | 492 | 516

#### Dec 23, 2016
Spring 5 and small tuning.

run | 1. | 2. | 3. | 4. | 5.
---|---|---|---|---|---
msg/s | 516 | 512 | 506 | 507 | 514

#### Dec 14, 2016
After message processor redesign.

run | 1. | 2. | 3. | 4. | 5.
---|---|---|---|---|---
msg/s | 501 | 484 | 507 | 492 | 513

#### Nov 17, 2016
run | 1. | 2. | 3. | 4. | 5.
---|---|---|---|---|---
msg/s | 471 | 470 | 480 | 480 | 481

### JMS

#### May 2, 2017
run | 1. | 2. | 3. | 4. | 5.
---|---|---|---|---|---
msg/s | 949 | 988 | 951 | 1009 | 978

#### Results on the old computer

#### Jan 16, 2017
Version 0.5-rc1 with JMS async sending setting.

run | 1. | 2. | 3. | 4. | 5.
---|---|---|---|---|---
msg/s | 565 | 567 | 538 | 561 | 552

#### Dec 14, 2016
After message processor redesign.

run | 1. | 2. | 3. | 4. | 5.
---|---|---|---|---|---
msg/s | 91 | 92 | 92 | 91 | 91

#### Nov 17, 2016
run | 1. | 2. | 3. | 4. | 5.
---|---|---|---|---|---
msg/s | 91 | 91 | 92 | 91 | 90

### AMQP results for the old load test

#### Nov 13, 2016
| run | messages/second
| --- | ---
| 1. | 533
| 2. | 549
| 3. | 561
| 4. | 546
| 5. | 551

### AMQP load test results for the old service written in Python (retested February 20, 2016):

Message broker, server and anti-virus program were on the same machine. Sending file is EICAR.

Client is on a different machine.

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

## Solr

As a storage for data was used USB 3 2.5" HDD.

Documents are Message info documents.

### Inserting documents

#### 1. million
 * In collection: 10 602 ms
 * In Solr: 59 769 ms
 * After commit: 65 834 ms

#### 201. million
 * In collection: 10 410 ms
 * In Solr: 55 889 ms
 * After commit: 273 645 ms
 
#### 251. million
 * In collection: 10 375 ms
 * In Solr: 49 748 ms
 * After commit: 346 980 ms

### Sorting queries

first hits

**created** field - ASC and then DESC

 x | 100 M | 200 M | 250 M
 --- | --- | --- | ---
 ASC | 2348 ms | 5797 ms | 6795 ms
 DESC | 1643 ms | 3549 ms | 4354 ms

### Commits

Insert one document and commit after.

With **empty** collection.

 x  | 10 | 100 | 1000 | 10 000 
--- | --- | --- | --- | ---
no  | 67 ms | 396 ms | 3 s | 21.4 s 
soft| 149 ms | 879 ms | 7.9 s | 1 m 6 s 
hard| 4.2 s | 29.8 s | 4 m 46 s | x 

With **100 million** documents (25 GB).

 x  | 10 | 100 | 1000 | 10 000 
--- | --- | --- | --- | ---
no  | 61 ms | 547 ms | 3.7 s | 21.9 s 
soft| x | 1.6 s | 11.3 s | 11 m 10 s 
hard| 8.5 s | 58.4 s | 9 m 42 s | x 

With **200 million** documents (46 GB).

 x  | 10 | 100 | 1000 | 10 000 
--- | --- | --- | --- | ---
no  | 76 ms | 532 ms | 3.7 s | 26.3 s 
soft| 4.9 s | 31.6 s | 3 m 38 s | 13 m 12 s 
hard| 9.2 s | 1 m 4 s | 10 m 43 s | x

With **250 million** documents (58 GB).

 x  | 10 | 100 | 1000 | 10 000 
--- | --- | --- | --- | ---
no  | 48 ms | 312 ms | 2.4 s | 19 s 
soft| 4.3 s | 29.5 s | 3 m 35 s | 26 m 2 s 
hard| 7.8 s | 1 m 17 s | x | x 
