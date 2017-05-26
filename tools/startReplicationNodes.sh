#!/usr/bin/env bash
#
# Runs replication nodes.
#

IMAGE='replication_node'

if [ $# -ne 1 ]
then
    count=2
else
    count=$1
fi

start=500
end=$((500 + count - 1))


for i in $(seq ${start} ${end})
do
    echo -n "Starting node${i}... "
    docker run --network=host -d -e "AVSERVICE_STORAGE_REPLICATION_NODEID=node${i}" \
        ${IMAGE} 2>&1 > /dev/null
    echo 'OK'
done

read -p 'Press enter to stop nodes' variable

docker kill $(docker ps -f ancestor=${IMAGE} -q) 2>&1 > /dev/null
