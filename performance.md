# Performance notes

Based on PC3 (NB, i3 2.40 GHz (2 cores), SSD) and everything runs on localhost.

## Servers

New prototype for a load test is prepared. It is a bit slower than the old
one but the old one was tuned several times to have better performance. 

AMQP server run command: `./gradlew runAmqpServer`

AMQP load test run command: `./gradlew runAmqpLoadTest`

JMS server run command: `./gradlew runJmsServer`

JMS load test run command: `./gradlew runJmsLoadTest`

Old load test run command: `./gradlew loadTest`

Log level: INFO

DB logging: no

First run is for a warm-up and is not counted.

### AMQP

#### Nov 13, 2016
run | messages/second
--- | ---
1. | 365
2. | 366
3. | 372
4. | 376
5. | 372

### JMS

#### Nov 13, 2016
run | messages/second
--- | ---
1. | 91
2. | 91
3. | 91
4. | 91
5. | 91

### AMQP results for the old load test

#### Nov 13, 2016
run | messages/second
--- | ---
1. | 533
2. | 549
3. | 561
4. | 546
5. | 551

#### Oct 28, 2016
run | messages/second
--- | ---
1. | 526
2. | 526
3. | 542
4. | 534
5. | 531

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

Insert document and commit after.

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
