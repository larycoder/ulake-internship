#!/bin/bash
cloc --exclude-list-file=deployment/cloc-ignore.conf --exclude-ext=sass,less $1 $2 $3 .
