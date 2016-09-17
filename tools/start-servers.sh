#!/bin/bash
#
# Start all docker containers with docker-compose up
#

ENV=docker
VIRT_ENVS=~/.virtenvs
PROJECT_DIR=~/projects/av-service


source $VIRT_ENVS/$ENV/bin/activate

cd $PROJECT_DIR/docker/

docker-compose up
