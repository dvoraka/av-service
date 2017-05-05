#!/usr/bin/env bash
#
# Tests project's Gradle tasks.
#

TASKS="
    configureEnvironment
    runAmqpCheck
    runAmqpServer
    #runAmqpFileServer
    #runAmqpFileServerWithReplication
    runAmqpToJmsBridge
    runAmqpReplicationService
    runCustomServer
    runJmsCheck
    runJmsServer
    runJmsToAmqpBridge
    "
SRC_ROOT=".."


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
        echo "Test failed for: ${task}"
        exit 1
    fi
    echo "OK"
done

echo "Tests OK"
