#!/usr/bin/env bash
#
# Docker commands for cleaning space on a disk
#

# Delete all containers
docker rm $(docker ps -a -q)

# Delete all images
docker rmi $(docker images -q)
