#!/bin/bash

# Predefine variable
HOST="ulake-openio";
PORT="6006";

# Start openio
docker run --detach --name $HOST -p $PORT:6006 openio/sds:latest;
