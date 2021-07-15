#!/bin/bash

docker run \
  --name ulake-openio \
  -v $PWD/openio/metadata-level0:/var/lib/oio/sds/OPENIO/meta0-0 \
  -v $PWD/openio/metadata-level1:/var/lib/oio/sds/OPENIO/meta1-0 \
  -v $PWD/openio/metadata-level2:/var/lib/oio/sds/OPENIO/meta2-0 \
  -v $PWD/openio/accounting:/var/lib/oio/sds/OPENIO/redis-0 \
  -v $PWD/openio/data:/var/lib/oio/sds/OPENIO/rawx-0 \
  -e SWIFT_CREDENTIALS="ulake:admin:abc123:.admin" \
  -d \
  openio/sds:20.04
