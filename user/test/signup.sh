#!/bin/bash

curl -v -X POST -H "Content-Type: application/json" -d '{"userName": "admin", "password": "1", "email": "admin@sontg.net"}' http://localhost:8785/api/user
