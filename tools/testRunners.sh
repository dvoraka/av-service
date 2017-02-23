#!/usr/bin/env bash

RUNNERS="
    runAmqpServer
    runJmsServer
    "
SRC_ROOT=".."


cd ${SRC_ROOT}

for runner in ${RUNNERS}
do
    echo | ./gradlew -q ${runner} > /dev/null
    if [ $? != 0 ]
    then
        echo "Test failed!"
        exit 1
    fi
done

echo "Tests OK"
