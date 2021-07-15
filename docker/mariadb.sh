#!/bin/bash

docker run \
  -p 23306:3306 \
  --name ulake-db \
  -e MARIADB_ROOT_PASSWORD=abc123 \
  -d mariadb:latest \
