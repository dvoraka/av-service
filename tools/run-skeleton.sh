#! /usr/bin/env bash
#
# Run script skeleton
#

SCRIPT_DIR=$(readlink -e "${BASH_SOURCE}")
echo $SCRIPT_DIR
BASE_DIR=$(dirname -- "${SCRIPT_DIR}")
echo $BASE_DIR

JAVA_OPTS=""
LOGGING_OPTS=""

LIB_PATH="${BASE_DIR}"/lib
echo $LIB_PATH


exit 0
