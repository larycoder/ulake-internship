#!/bin/bash 

token=`curl -s -X POST -H "Content-Type: application/json" -d '{"userName":"dx", "password":"1"}'  http://user.ulake.usth.edu.vn/api/auth/login | grep -o "resp.*" | sed  's/.*":"//;s/"}$//'`

echo This script should be sourced to get JWT exported.
echo $token
if [ "`command -v xclip`" != "" ]; then
	echo $token | xclip
fi

export JWT=$token
