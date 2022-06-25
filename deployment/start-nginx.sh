#!/bin/bash

# Predefine variable
HOST="ulake-nginx";
PORT="8080";
BASE_DIR=$(readlink -f $(dirname $0));
ROOT_DIR=$(readlink -f $BASE_DIR/../);
CONF="$BASE_DIR/nginx.conf";
NET="ulake-network";
FRONTEND=""

# Parse args
while test ${#} -gt 0; do
    case "$1" in
        --help) echo "start-nginx.sh [-p PORT] [frontend-name]"
                ;;
        -p)     shift
                PORT="$1"
                ;;
        *)      FRONTEND="$1"
                ;;
    esac
    shift
done

if [ "$FRONTEND" == "" ]; then
    # Start nginx docker with ALL frontends mounted
    FRONTENDS=""
    for i in dashboard common admin table; do
        FRONTENDS="$FRONTENDS -v $ROOT_DIR/$i/html:/opt/$i:ro "
    done
    docker run --name $HOST -p $PORT:80 \
        -v $CONF:/etc/nginx/nginx.conf:ro \
        $FRONTENDS \
        --network $NET \
        -d nginx:latest
else
    # Start nginx docker with a specific port and configurations
    docker run --name $HOST-$FRONTEND -p $PORT:80 \
        -v $CONF:/etc/nginx/nginx.conf:ro \
        -v $ROOT_DIR/$FRONTEND/html:/opt/$FRONTEND:ro \
        --network $NET \
        -d nginx:latest
fi