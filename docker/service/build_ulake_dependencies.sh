#!/bin/bash

SCRIPT_PATH="";
SRC="";
DEST="";

MARKER="build.gradle";
CONFIG_FILES="build.gradle gradle.properties settings.gradle";
GRADLE_RUNNER="gradlew gradle";

################# script #################

SCRIPT_PATH=$(dirname $0);
SCRIPT_PATH=$(cd $SCRIPT_PATH && pwd);

SRC=$(cd $SCRIPT_PATH/../../ && pwd);
DEST=$(cd $SCRIPT_PATH && pwd)"/ulake";

################# function #################

function clean_dest { # parameters: {TARGET}
    MY_TARGET=$1;
    if [[ ! -d $MY_TARGET ]];
    then
        mkdir -p $MY_TARGET;
    fi;

    if [[ $MY_TARGET != "" ]];
    then
        rm -rf $MY_TARGET/*;
        for i in $MY_TARGET/.*;
        do
            if [[ $i != "$MY_TARGET/." && $i != "$MY_TARGET/.." ]];
            then
                rm -rf $i;
            fi;
        done;
    fi;
}

function cp_src_to_dest { # parameters: {SRC} {DEST} {LIST OF FILE}
    MY_SRC=$1;
    MY_DEST=$2;
    shift 2;
    clean_dest $MY_DEST;
    for i in $@;
    do
        cp -r $MY_SRC/$i $MY_DEST/$i;
    done
}

function discovery_and_cp_src {
    # copy root
    cp_src_to_dest $SRC $DEST $GRADLE_RUNNER $CONFIG_FILES;

    # copy sub-folder
    for i in $(ls -a $SRC);
    do
        if [[ -f $SRC/$i/$MARKER && $i != "." && $i != ".." ]];
        then
            cp_src_to_dest $SRC/$i $DEST/$i $CONFIG_FILES;
        fi;
    done;
}

function compress_config {
    cd $(dirname $DEST);
    tar cfv ulake.tar ulake;
}

################# main #################

discovery_and_cp_src;
compress_config;
rm -rf $DEST;
