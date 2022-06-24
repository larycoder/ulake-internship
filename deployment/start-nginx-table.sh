#!/bin/bash

# Start nginx docker with proper configuration and installation

# Predefine variable
HOST="ulake-nginx-table";
PORT="28080";
BASE_DIR=$(readlink -f $(dirname $0));
ROOT_DIR=$(readlink -f $BASE_DIR/../);
CONF="$BASE_DIR/nginx-table.conf";
TABLE="$ROOT_DIR/table/html";


docker run --name $HOST -p $PORT:80 \
    --network="ulake-network" \
    -v $CONF:/etc/nginx/nginx.conf:ro \
    -v $TABLE:/opt/table:ro \
    -d nginx:latest
