#!/bin/bash

/home/vscode/bin/oauth2-proxy/oauth2-proxy --config=/workspaces/ulake/deployment/oauth2-proxy/oauth2-proxy.cfg

vncserver

websockify -D --web=/usr/share/novnc/ 6080 localhost:5901
