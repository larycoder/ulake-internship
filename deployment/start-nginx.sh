#!/bin/bash

# Start nginx docker with proper configuration and installation

# Predefine variable
HOST="ulake-nginx";
PORT="8080";
BASE_DIR=$(readlink -f $(dirname $0));
ROOT_DIR=$(readlink -f $BASE_DIR/../);
CONF="$BASE_DIR/nginx.conf";

DASHBOARD="$ROOT_DIR/dashboard/html";
ADMIN="$ROOT_DIR/admin/html";

docker run --name $HOST -p $PORT:80 \
    -v $CONF:/etc/nginx/nginx.conf:ro \
    -v $DASHBOARD:/opt/dashboard:ro \
    -v $ADMIN:/opt/admin:ro \
    -d nginx:latest
