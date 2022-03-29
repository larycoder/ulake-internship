#!/bin/bash

# Predefine variable
HOST="ulake-phpmyadmin";
PORT="8081";
PASSWORD="root"; # for root user
SQL_HOST="172.17.0.1"; # default machine host in docker
SQL_PORT="23306"; # follow start-mysql.sh

# Start phpmyadmin
docker run --detach --name $HOST \
    --env MYSQL_ROOT_PASSWORD="$PASSWORD" \
    --env PMA_HOST=$SQL_HOST \
    --env PMA_PORT=$SQL_PORT \
    -p $PORT:80 phpmyadmin:latest;
