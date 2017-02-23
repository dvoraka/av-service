#!/usr/bin/env bash
#
# Tests project's Gradle tasks.
#

RUNNERS="
    configureEnvironment
    runAmqpCheck
    runAmqpServer
    runAmqpToJmsBridge
    runCustomServer
    runJmsCheck
    runJmsServer
    runJmsToAmqpBridge
    "
SRC_ROOT=".."


cd ${SRC_ROOT}

for runner in ${RUNNERS}
do
    echo -n "Checking "${runner}"... "
    echo | ./gradlew -q ${runner} > /dev/null 2>&1
    if [ $? != 0 ]
    then
        echo "Test failed for:" ${runner}
        exit 1
    fi
    echo "OK"
done

echo "Tests OK"
