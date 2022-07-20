#!/bin/bash

NET="ulake-network"

function backend() {
  docker run -d --rm --name ulake-sensu-backend \
    --network $NET \
    -v sensu-backend-data:/var/lib/sensu \
    sensu/sensu sensu-backend start;
}

function agent() {
  docker run -d --rm --name ulake-sensu-agent \
    --network $NET \
    sensu/sensu sensu-agent start \
    --backend-url ws://ulake-sensu-backend:8081 \
    --deregister \
    --keepalive-interval=5 \
    --keepalive-warning-timeout=10 \
    --subscriptions linux;
}

case $1 in
  "backend")
    backend;
    ;;
  "agent")
    agent;
    ;;
  *)
    echo "Usage: ./start-sensu.sh <TYPE>";
    echo "";
    echo "TYPE:";
    echo "";
    echo "  backend : start backend";
    echo "  agent   : start agent";
    echo "";
    ;;
esac
