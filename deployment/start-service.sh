#!/bin/bash

# Start service docker with proper configuration and installation

# Predefine variable
HOST="ulake-service";
NET="ulake-network"
BASE_DIR=$(readlink -f $(dirname $0));
ROOT_DIR=$(readlink -f $BASE_DIR/../);
QUARKUS_SERVICE="";

# Parse args
while test ${#} -gt 0; do
    case "$1" in
        -s) shift
            QUARKUS_SERVICE="$1"
            ;;
        --help) echo "start-service.sh [-s service]"
                exit
                ;;
    esac
    shift
done

# Export ports
#PORTS="8781-8799";
#EXP_PORT="";
#for i in $PORTS;
#do
#    EXP_PORT="$EXP_PORT -p $i:$i";
#done;

# Quarkus service
if [[ $QUARKUS_SERVICE != "" ]];
then
    HOST="$HOST-$QUARKUS_SERVICE";
fi;

# LRA Coordinator
#docker run --name ulake-lra -p 8793:8080 --network $NET -d jbosstm/lra-coordinator

# Main services
docker run --name $HOST \
    -v $ROOT_DIR:/home \
    -e QUARKUS_SERVICE=$QUARKUS_SERVICE \
    --network $NET \
    -d ulake/service:1.0.0-SNAPSHOT
