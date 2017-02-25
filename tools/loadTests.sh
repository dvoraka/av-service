#!/usr/bin/env bash
#
# Runs AMQP and JMS load tests. The default is 3 loops and you can give another count
# as a first program's argument.
#

SRC_ROOT=".."

loops=3


cd ${SRC_ROOT}

function runTask {
    for loop in `seq $1`
    do
        ./gradlew -q $2 2> /dev/null
        if [ $? -ne 0 ]
        then
            echo "Test failed for: "$2
            exit 1
        fi
        echo "--------"
    done
}

if [ $# -ne 1 ]
then
    loops=3
else
    loops=$1
fi

echo "Run AMQP load test with ${loops} loops..."
runTask ${loops} runAmqpLoadTest

echo "Run JMS load test with ${loops} loops..."
runTask ${loops} runJmsLoadTest

echo "Tests OK"
