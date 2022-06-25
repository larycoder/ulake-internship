#!/bin/bash

# Predefine variable
HOST="ulake-nginx";
PORT="8080";
BASE_DIR=$(readlink -f $(dirname $0));
ROOT_DIR=$(readlink -f $BASE_DIR/../);
CONF="$BASE_DIR/nginx.conf";
NET="ulake-network";

if [ "$1" == "" ]; then
    # Start nginx docker with ALL configurations and installations
    DASHBOARD="$ROOT_DIR/dashboard/html";
    COMMON="$ROOT_DIR/common/html";
    ADMIN="$ROOT_DIR/admin/html";
    TABLE="$ROOT_DIR/table/html";
    docker run --name $HOST -p $PORT:80 \
        -v $CONF:/etc/nginx/nginx.conf:ro \
        -v $DASHBOARD:/opt/dashboard:ro \
        -v $COMMON:/opt/common:ro \
        -v $ADMIN:/opt/admin:ro \
        -v $TABLE:/opt/table:ro \
        --network $NET \
        -d nginx:latest
else
    # Start nginx docker with a specific port and configurations
    docker run --name $HOST-$1 -p $PORT:80 \
        -v $CONF:/etc/nginx/nginx.conf:ro \
        -v $ROOT_DIR/$1/html:/opt/dashboard:ro
        --network $NET \
        -d nginx:latest
fi