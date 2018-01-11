#!/usr/bin/env bash
#
# Tests project's Gradle tasks.
#

TASKS="
    configureEnvironment
    runAmqpCheck
    runAmqpLoadTest
    runAmqpBufferedLoadTest
    runAmqpConcurrentLoadTest
    runAmqpCheckServer
    runAmqpFileServer
    runAmqpFileServerWithReplication
    runAmqpToJmsBridge
    runAmqpReplicationService
    runCustomServer
    runJmsCheck
    runJmsLoadTest
    runJmsBufferedLoadTest
    runJmsConcurrentLoadTest
    runJmsCheckServer
    runJmsFileServer
    runJmsToAmqpBridge
    runKafkaCheck
    runKafkaLoadTest
    runKafkaBufferedLoadTest
    runKafkaConcurrentLoadTest
    runKafkaCheckServer
    runKafkaFileServer
    "
SRC_ROOT=".."

# export message count for load tests
export AVSERVICE_PERF_MSGCOUNT=1


cd ${SRC_ROOT}

for task in ${TASKS}
do
    if [[ ${task:0:1} == '#' ]]
    then
        continue
    fi

    echo -n "Checking ${task}... "
    echo | ./gradlew -q ${task} > /dev/null 2>&1
    if [ $? != 0 ]
    then
        echo
        echo "Test failed for: ${task}"
        exit 1
    fi
    echo "OK"
done

echo "Tests OK"
