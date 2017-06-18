#!/usr/bin/env bash
#
# Tool for removing all Docker containers and images.
#

# Delete all containers
echo "Removing all containers..."
docker rm $(docker ps -a -q)
echo "Done."

# Delete all images
echo "Removing all images..."
docker rmi $(docker images -q)
echo "Done."
