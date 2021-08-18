#!/bin/bash

curl -v -X POST -H "Content-Type: application/json" -d '{"userName": "admin", "password": "1"}' http://localhost:8785/api/auth/login
