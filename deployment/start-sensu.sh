#!/bin/bash

NET="ulake-network"
BACKEND_HOST="ulake-sensu"

DEFAULT_USERNAME="root"
DEFAULT_PASSWORD="root"

BASE_DIR=$(readlink -f $(dirname $0));
ROOT_DIR=$(readlink -f $BASE_DIR/../);
CONF="$BASE_DIR/sensu_backend.yml";
SWARM_CONF="$BASE_DIR/docker_compose_sensu_agents_v3.yml"

function backend() {
  docker stop ulake-sensu;
  docker run -d --rm --name $BACKEND_HOST \
    --network $NET \
    -p 28080:8080 \
    -e SENSU_BACKEND_CLUSTER_ADMIN_USERNAME=$DEFAULT_USERNAME \
    -e SENSU_BACKEND_CLUSTER_ADMIN_PASSWORD=$DEFAULT_PASSWORD \
    -v $CONF:/etc/sensu/backend.yml \
    -v sensu-backend-data:/var/lib/sensu \
    sensu/sensu sensu-backend start;
}

function agent() {
  docker stop ulake-sensu-agent;
  docker run -d --rm --name ulake-sensu-agent \
    --network $NET \
    sensu/sensu sensu-agent start \
    --backend-url ws://$BACKEND_HOST:8081 \
    --deregister \
    --keepalive-interval=5 \
    --keepalive-warning-timeout=10 \
    --subscriptions linux;
}

function agent_service() {
  docker stack deploy -c $SWARM_CONF ulake-hadoop
}

case $1 in
  "backend")
    backend;
    ;;
  "agent")
    agent;
    ;;
  "swarm")
    agent_service;
    ;;
  *)
    echo "Usage: ./start-sensu.sh <TYPE>";
    echo "";
    echo "TYPE:";
    echo "";
    echo "  backend : start backend";
    echo "  agent   : start agent";
    echo "  swarm   : start agent in swarm cluster";
    echo "";
    ;;
esac
