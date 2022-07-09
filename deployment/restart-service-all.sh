#!/bin/bash

containers=$( docker ps --format "{{.Names}}" -a | grep "^ulake-service" | paste -sd" " )
docker restart $containers