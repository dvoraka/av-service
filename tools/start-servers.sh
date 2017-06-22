#!/usr/bin/env bash
#
# Starts all development Docker containers with the "docker-compose up" command.
#
# It uses Python virtual environment because of docker-compose installation.
#

# the Python virtual environment name with installed docker-compose
ENV=docker
# the root of all virtual environments
VIRT_ENVS=~/.virtenvs
# the root of the project sources
PROJECT_DIR=~/projects/av-service


# activate the virtual environment
source ${VIRT_ENVS}/${ENV}/bin/activate

cd ${PROJECT_DIR}/docker/

# start containers
docker-compose up

echo
echo "Removing replication containers..."
docker rm docker_replication-node-1000_1
docker rm docker_replication-node-1001_1
echo "Done."
