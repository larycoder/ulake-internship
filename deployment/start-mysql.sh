#!/bin/bash

# Predefine variable
HOST="ulake-mysql";
PORT="23306";
PASSWORD="root"; # for root user
SQL="./init.sql";
TIME=10; # temporary wait for waiting server up

# Start newest mariadb and initialize database
docker run --detach --name $HOST \
    --env MYSQL_ROOT_PASSWORD="$PASSWORD" \
    -p $PORT:3306 mariadb:latest && \
    sleep $TIME && \
    docker exec -i $HOST mysql -u root < $SQL;
