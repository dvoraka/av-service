#! /bin/sh

# enable TCP socket for ClamAV daemon
echo "TCPSocket 3310" >> /etc/clamav/clamd.conf
echo "TCPAddr 127.0.0.1" >> /etc/clamav/clamd.conf

