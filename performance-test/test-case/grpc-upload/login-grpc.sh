#!/bin/bash

HOST="http://dashboard.ulake.usth.edu.vn/api/grpc/grpc-login"

if [[ $1 != "" ]]; then
	AUTH=$1
fi

curl -v --http2-prior-knowledge -X 'POST' \
  "$HOST" \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "userName": "admin",
  "password": "admin"
}'
