#!/bin/bash

# Path to the configuration file in the upper directory
CONFIG_FILE="/home/novete36/ulake/performance-test/test-case/upload.config"

# Function to read the bearer token from the configuration file
get_bearer_token() {
    if [[ -f $CONFIG_FILE ]]; then
        # Source the configuration file to get the AUTH variable
        source $CONFIG_FILE
    else
        echo "Configuration file not found: $CONFIG_FILE"
        exit 1
    fi
}

# Get the bearer token from the configuration file
get_bearer_token

HOST="http://dashboard.ulake.usth.edu.vn/api/object/grpc-file"
#AUTH="eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3NvbnRnLm5ldC9pc3N1ZXIiLCJ1cG4iOiJ0dW5nbmd2aWV0MzZAZ21haWwuY29tIiwiZ3JvdXBzIjpbIlVzZXIiLCJBZG1pbiJdLCJhdXRoX3RpbWUiOjE3MTgwNzM1MjI1MTIsInN1YiI6IjEiLCJpYXQiOjE3MTgwNzM1MjIsImV4cCI6MTcxODE1OTkyMiwianRpIjoiZjExNDI3ZWItZjk3YS00MmE3LThlOGUtMWUxNjk4MDVlMTkxIn0.gVdYCS-Zpyu9OFwUcZVp0U_LtuOar_EOMdDg7EZ8Xg0XJqA18MF_JpxJSG3xVH0MJu8i2ybXZPfN7Xp8XW961OYugXlEf10B2nJnTdMS0EFh1R7E9RJDFwN065A1WAxmthhdWssqu6Ymcgpex4zZxwGTKFnkRXRruj6vtAB2yDUBJS2VsDxFBuvtKzh_ZFh3dGT-pWlRVNJreMEuIWnrjWBwkLQF7sh1kjjUIECvF70X2rOX_XbKCugyAPDc36-_6KU6a4kh-kdC3-wUoPMOFo85e7NpbTbJBwMDGraHq7k6kbl25SUOplKDbxqaaaarZ_7JyB1Y0ZF12oU8kgnyug"
#HOST="localhost:5000"

if [[ $1 != "" ]]; then
	AUTH=$1
fi

curl -v --http2-prior-knowledge -X 'POST' \
  "$HOST" \
  -H "Authorization: Bearer $AUTH" \
  -H 'Content-Type: multipart/form-data' \
  -F 'fileInfo={ "mime": "text/plain", "name": "test_file", "ownerId": 2004, "size": 813050 };type=application/json' \
  -F 'file=@/home/novete36/Downloads/test_file.txt;type=application/octet-stream'

