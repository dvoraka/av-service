#!/bin/bash
#
# Starts all docker containers with "docker-compose up" command
#

# virtual environment name
ENV=docker
# root of all virtual environments
VIRT_ENVS=~/.virtenvs
# root of project
PROJECT_DIR=~/projects/av-service


source $VIRT_ENVS/$ENV/bin/activate

cd $PROJECT_DIR/docker/

docker-compose up
