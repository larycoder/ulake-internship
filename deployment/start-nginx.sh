#!/bin/bash

# Start nginx docker with proper configuration and installation

# Predefine variable
HOST="ulake-nginx";
PORT="8080";
BASE_DIR=$(readlink -f $(dirname $0));
ROOT_DIR=$(readlink -f $BASE_DIR/../);
CONF="$BASE_DIR/nginx.conf";
DATA="$ROOT_DIR/dashboard";

docker run --name $HOST -p $PORT:80 \
    -v $CONF:/etc/nginx/nginx.conf:ro \
    -v $DATA:/opt:ro \
    -d nginx:latest
