#!/usr/bin/env bash
#
# Tool for removing all Docker containers and images.
#

# delete all containers
echo "Removing all containers..."
docker rm $(docker ps -a -q)
echo "Done."

# delete all images
echo "Removing all images..."
docker rmi $(docker images -q)
echo "Done."
