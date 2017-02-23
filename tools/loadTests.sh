#!/usr/bin/env bash

SRC_ROOT=".."


cd ${SRC_ROOT}

loops=3

function runTask {
    for loop in `seq $1`
    do
        ./gradlew -q $2 2> /dev/null
        if [ $? != 0 ]
        then
            exit 1
        fi
        echo "--------"
    done
}

echo "Run AMQP load test with ${loops} loops..."
runTask ${loops} runAmqpLoadTest

echo "Run JMS load test with ${loops} loops..."
runTask ${loops} runJmsLoadTest

echo "Tests OK"
