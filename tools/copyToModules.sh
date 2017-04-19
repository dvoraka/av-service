#!/usr/bin/env bash
#
# Copy anything to all modules
#

ROOT=$(dirname $(pwd))

# modules
MODULES=$(cat $ROOT/settings.gradle | cut -d\' -f 2 | grep -v service)
# where to copy
CONFIG_PATH=src/main/resources
# what copy
CONFIG=$ROOT/service/$CONFIG_PATH/logback.xml


echo "Copying config files..."

for module in $MODULES
do
    mkdir $ROOT/$module/$CONFIG_PATH
    cp -v $CONFIG $ROOT/$module/$CONFIG_PATH
done

echo
echo "Done."
