#! /bin/sh

# enable TCP socket for ClamAV daemon
echo "TCPSocket 3310" >> /etc/clamav/clamd.conf
echo "TCPAddr 127.0.0.1" >> /etc/clamav/clamd.conf

# enable main instance for ActiveMQ
ln -s /etc/activemq/instances-available/main /etc/activemq/instances-enabled/main
