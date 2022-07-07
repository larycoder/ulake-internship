#!/bin/bash

cd /home;

if [[ $QUARKUS_SERVICE == "" ]];
then
    ./gradlew dev;
else
    ./gradlew $QUARKUS_SERVICE:quarkusDev;
fi;
