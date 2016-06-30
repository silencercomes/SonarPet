#!/bin/bash

if [ ! which python3 >/dev/null 2>&1 ]; then
    echo "Python 3 isn't found!"
    echo "Please install python 3 to generate the sources!"
    exit 1;
fi;


if [ ! which cpp >/dev/null 2>&1 ]; then
    echo "No C preprocessor found!"
    echo "Please install a C preprocessor to generate the sources!"
    exit 1;
fi;

python3 preprocessTemplates.py v1_8_R3 v1_9_R1 v1_9_R2 v1_10_R1 || exit 1
