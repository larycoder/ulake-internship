#!/bin/bash

# Start service docker with proper configuration and installation

# Predefine variable
HOST="ulake-service";
NET="ulake-network"
BASE_DIR=$(readlink -f $(dirname $0));
ROOT_DIR=$(readlink -f $BASE_DIR/../);

# Export ports
PORTS="8781-8788";
EXP_PORT="";
for i in $PORTS;
do
    EXP_PORT="$EXP_PORT -p $i:$i";
done;

docker run --name $HOST $EXP_PORT \
    -v $ROOT_DIR:/home \
    --network $NET \
    -d ulake/service:1.0.0-SNAPSHOT
