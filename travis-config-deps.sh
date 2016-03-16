#! /bin/sh

# set TCP socket for ClamAV daemon
sed -i 's/^LocalSocket.*$/#/' /etc/clamav/clamd.conf
sed -i '1i\
TCPSocket 3310\
TCPAddr 127.0.0.1' /etc/clamav/clamd.conf

