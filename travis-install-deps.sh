#! /bin/sh

# update data
apt-get update

# install ClamAV daemon
apt-get install -y clamav-daemon

# install ActiveMQ
apt-get install -y activemq
