#!/bin/bash

projects=$(cat ../settings.gradle  | grep include | sed "s/'//g;s/include //;s/,//g")
projects=($projects)
echo ${projects[@]}
for i in ${projects[@]}; do
    echo Starting $i....
    ./start-service.sh -s $i
done