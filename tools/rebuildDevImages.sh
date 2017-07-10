#!/usr/bin/env bash
#
# Rebuilds development Docker images.
#

IMAGES="replication-node"
DOCKER_DIR=~/projects/av-service/docker


echo "Removing images..."
for IMAGE in ${IMAGES}
do
    docker rmi ${IMAGE}
    sleep 4
done
echo "Done."
echo

echo "Building replication node..."
cd ${DOCKER_DIR}/replicationnodesnap
docker build --no-cache -t replication-node .
echo "Done."
