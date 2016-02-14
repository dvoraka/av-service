#!/bin/bash
#
# prepares basic RabbitMQ structure
#

# add user guest with password guest
rabbitmqctl add_user guest guest

# create virtual host antivirus
rabbitmqctl add_vhost antivirus

# set all permissions on antivirus for user guest
rabbitmqctl set_permissions -p antivirus guest ".*" ".*" ".*"


exit 0
