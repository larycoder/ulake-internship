#!/bin/bash

# Start service docker with proper configuration and installation

# Predefine variable
HOST="ulake-service";
NET="ulake-network"
BASE_DIR=$(readlink -f $(dirname $0));
ROOT_DIR=$(readlink -f $BASE_DIR/../);
QUARKUS_SERVICE="";
NATIVE=""
NATIVE_CONTAINER="";

# Parse args
while test ${#} -gt 0; do
    case "$1" in
        -s) shift
            QUARKUS_SERVICE="$1"
            ;;
        -n) shift
            NATIVE="YES"
            ;;
        --help) echo "start-service.sh -s serviceName [-n]"
                echo "Option: -n for Native service"
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
if [[ $QUARKUS_SERVICE != "" ]]; then
    HOST="$HOST-$QUARKUS_SERVICE";
else
    echo "Service name is required!"
    echo "start-service.sh -s serviceName"
fi;

# LRA Coordinator
#docker run --name ulake-lra -p 8793:8080 --network $NET -d jbosstm/lra-coordinator

# Main services
if [[ "$NATIVE" == "" ]]; then
    docker run --name $HOST \
        -v $ROOT_DIR/common/src:/home/common/src \
        -v $ROOT_DIR/$QUARKUS_SERVICE:/home/$QUARKUS_SERVICE \
        -e QUARKUS_SERVICE=$QUARKUS_SERVICE \
        --network $NET \
        -d ulake/service:1.0.0-SNAPSHOT
else
    RUNNER=`echo $ROOT_DIR/$QUARKUS_SERVICE/build/*-runner`
    TARGET_RUNNER="/home/ulake-service-$QUARKUS_SERVICE-runner"
    docker run -it --name $HOST \
        -v $RUNNER:$TARGET_RUNNER \
        --network $NET \
        --rm \
        --entrypoint $TARGET_RUNNER \
        registry.access.redhat.com/ubi8/ubi-minimal:8.6
fi