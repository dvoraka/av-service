#!/bin/bash
#
# Prepares basic RabbitMQ configuration.
#

# add user guest with password guest
rabbitmqctl add_user guest guest

# create virtual host antivirus
rabbitmqctl add_vhost antivirus

# set all permissions on antivirus for user guest
rabbitmqctl set_permissions -p antivirus guest ".*" ".*" ".*"
