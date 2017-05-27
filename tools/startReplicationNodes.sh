#!/usr/bin/env bash
#
# Runs replication nodes.
#
# First argument is number of containers (default 2).
#
# IMAGE is Docker image name.
#

IMAGE='replication_node'

if [ $# -ne 1 ]
then
    count=2
else
    count=$1
fi

start=500
end=$((start + count - 1))


echo 'Starting nodes:'
for i in $(seq ${start} ${end})
do
    node_name=node${i}
    echo -n " * ${node_name}... "
    docker run \
        --network=host \
        -d \
        --rm \
        -e "AVSERVICE_STORAGE_REPLICATION_NODEID=${node_name}" \
        --name ${node_name} \
        ${IMAGE} 2>&1 > /dev/null

    if [ $? -ne 0 ]
    then
        exit 1
    fi

    echo 'OK'

    sleep 3
done

echo

read -p 'Press enter to stop the nodes' _
echo

echo -n 'Stopping nodes... '
docker kill $(docker ps -f ancestor=${IMAGE} -q) 2>&1 > /dev/null
echo 'OK'
