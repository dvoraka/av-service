#!/usr/bin/env bash
#
# Docker commands for cleaning space
#

# Delete all containers
docker rm $(docker ps -a -q)

# Delete all images
docker rmi $(docker images -q)
