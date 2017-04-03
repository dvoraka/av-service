#!/usr/bin/env bash
#
# Prints out all used Spring profiles in the project.
#

SRC_ROOT=".."

# find all Java files
find ${SRC_ROOT} -name "*.java" -exec cat {} \; | \
    # find all @Profile annotations
    grep -E '.*@Profile(.+).*' | \
    # clean the output
    sed -e 's/.*\(@.*)\)/\1/g' | \
    sed -e 's/@Profile("\(.\+\)")/\1/g' | \
    grep -v '!' | \
    # sort the output
    sort | \
    # remove duplicities from the output
    uniq
