#!/bin/bash

docker run \
  --name ulake-openio \
  -e OPENIO_IFDEV=br0 --net=host \
  -d \
  openio/sds
