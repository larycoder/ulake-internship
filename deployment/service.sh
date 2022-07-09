#!/bin/bash

# Predefine variable
HOST="ulake-service";
NET="ulake-network"
BASE_DIR=$(readlink -f $(dirname $0));
ROOT_DIR=$(readlink -f $BASE_DIR/../);


help() {
    echo "Usage: service.sh [start/start-all/restart/kill]"
}

# Start service docker with proper configuration and installation
start() {
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
            --help) echo "service.sh start -s serviceName [-n]"
                    echo "Option: -n for Native service"
                    exit
                    ;;
        esac
        shift
    done

    # Quarkus service
    if [[ $QUARKUS_SERVICE != "" ]]; then
        HOST="$HOST-$QUARKUS_SERVICE";
    else
        echo "Service name is required!"
    fi;

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
        docker run -d --name $HOST \
            -v $RUNNER:$TARGET_RUNNER \
            --network $NET \
            --rm \
            --entrypoint $TARGET_RUNNER \
            registry.access.redhat.com/ubi8/ubi-minimal:8.6
    fi
}

start_all() {
    projects=$(cat ../settings.gradle  | grep include | sed "s/'//g;s/include //;s/,//g")
    projects=($projects)
    echo ${projects[@]}
    for i in ${projects[@]}; do
        echo Starting {$i}....
        if [[ "$i" != "common" ]]; then
            start -s $i $@
        fi
    done
}

kill_all () {
    containers=$( docker ps --format "{{.Names}}" -a | grep "^ulake-service" | paste -sd" " )
    docker stop $containers
    docker rm $containers
}

restart () {
    containers=$( docker ps --format "{{.Names}}" -a | grep "^ulake-service" | paste -sd" " )
    docker restart $containers
}

case $1 in
    "") help
        ;;

    "start")
        shift
        start $@
        ;;

    "start-all")
        start_all $@
        ;;

    "restart")
        restart $@
        ;;

    "kill")
        kill_all $@
        ;;
esac

