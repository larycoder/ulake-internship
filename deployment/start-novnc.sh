#!/bin/bash


vncserver

websockify -D --web=/usr/share/novnc/ 6080 localhost:5901
