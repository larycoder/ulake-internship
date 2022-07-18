#!/bin/bash

cd /home;
tar -xvf ulake.tar;
mv ulake/* .;
mv ulake/.* .;
rm ulake.tar;
./gradlew jar;
