#!/bin/bash

# Start nginx docker with proper configuration and installation

# Predefine variable
HOST="ulake-nginx";
PORT="80";
BASE_DIR=$(pwd $(dirname $0));
CONF="$BASE_DIR/nginx.conf";

docker run --name $HOST -p $PORT:80 \
    -v $CONF:/etc/nginx/nginx.conf:ro\
    -d nginx:latest
