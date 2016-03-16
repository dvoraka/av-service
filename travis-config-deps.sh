#! /bin/sh

# set TCP socket for ClamAV daemon
sed -i 's/^LocalSocket.*$/#/' /etc/clamav/clamd.conf
echo "TCPSocket 3310" >> /etc/clamav/clamd.conf
echo "TCPAddr 127.0.0.1" >> /etc/clamav/clamd.conf

