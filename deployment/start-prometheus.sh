#!/bin/bash

# Define common variables
PROMETHEUS_YML_PATH="./prometheus/prometheus.yml"
PROMETHEUS_DATA_VOLUME="prometheus-data"
GRAFANA_DATA_VOLUME="grafana-data"
NETWORK_NAME="ulake-network"
TIME=3  # Wait time for services to start

# Ensure the Docker network exists
docker network inspect $NETWORK_NAME >/dev/null 2>&1 || \
    docker network create $NETWORK_NAME

# Start Node Exporter
echo "Starting Node Exporter..."
docker run --detach \
  --name node-exporter \
  --restart unless-stopped \
  --volume /etc/node-exporter.yml:/etc/node-exporter.yml:ro \
  --pid=host \
  --network=$NETWORK_NAME \
  --publish 9100:9100 \
  quay.io/prometheus/node-exporter:latest


if [ $? -ne 0 ]; then
    echo "Failed to start Node Exporter"
    exit 1
fi

# Start Prometheus
echo "Starting Prometheus..."
docker run --detach \
  --name prometheus \
  --restart unless-stopped \
  --volume $PROMETHEUS_YML_PATH:/etc/prometheus/prometheus.yml \
  --volume $PROMETHEUS_DATA_VOLUME:/prometheus \
  --publish 9090:9090 \
  --network=$NETWORK_NAME \
  prom/prometheus:latest \
  --config.file=/etc/prometheus/prometheus.yml \
  --web.enable-lifecycle \
  --web.enable-remote-write-receiver

if [ $? -ne 0 ]; then
    echo "Failed to start Prometheus"
    exit 1
fi

# Start Grafana
echo "Starting Grafana..."
docker run --detach \
    --name grafana \
    --restart unless-stopped \
    --volume $GRAFANA_DATA_VOLUME:/var/lib/grafana \
    --publish 3000:3000 \
    --network=$NETWORK_NAME \
    grafana/grafana-enterprise

if [ $? -ne 0 ]; then
    echo "Failed to start Grafana"
    exit 1
fi

# Wait for a few seconds to ensure services are up
echo "Waiting for $TIME seconds to ensure services are up..."
sleep $TIME

echo "Services started successfully."
