#!/bin/bash
# WARNING: script should be run on place having ssh for all node in cluster
# as frontend node.

DOCKER_HADOOP_URI="https://github.com/larycoder/docker-hadoop.git";

usage() {
    echo "Usage: start-hdfs.sh <TYPE> <USERNAME> <IP_FILE> <CONF_DIR>";
    echo "";
    echo "PARAMETERS:";
    echo "";
    echo "  TYPE: machine node type [ namenode, datanode ].";
    echo "";
    echo "  USERNAME: username used to perform ssh access into setup machine.";
    echo "";
    echo "  IP_FILE: the file holds all deployment machine separated by newline.";
    echo "           And each line will have format: <Machine IP> <SWARM IP>";
    echo "           Note that namenode will only be start once at first addr";
    echo "           of IP_FILE.";
    echo "";
    echo "  CONF_DIR: machine directory for install all necessary files for run";
    echo "            datanode ( must be absolute path ).";
    echo "";
}

prepare() {
    SSH="ssh -n $1@$2"; # username@address
    BASE=$4; # configuration directory

    RET=$($SSH "if [[ -d $BASE/docker-hadoop ]]; then echo 1; else echo 0; fi");
    if [[ $RET == 1 ]];
    then
        echo "Folder docker-hadoop is already exited for $2...";
    else
        echo "Prepare parent directory of $2...";
        echo $SSH "mkdir -p $BASE";

        echo "Clone docker-hadoop to $BASE of $2...";
        $SSH "cd $BASE && git clone $DOCKER_HADOOP_URI";
    fi;
}

start_datanode() {
    SSH="ssh -n $1@$2"; # username@address
    ULAKE_NET_IP=$3; # datanode address should be static
    BASE="$4/docker-hadoop/deployment"; # configuration directory

    echo "Start datanode at $2...";
    $SSH "cd $BASE && ./start-datanode.sh ulake-Hdatanode_$2_$3 $3";
}

start_namenode() {
    SSH="ssh -n $1@$2"; # username@address
    ULAKE_NET_IP=$3; # datanode address should be static
    BASE="$4/docker-hadoop/deployment"; # configuration directory

    echo "Start namenode at $2...";
    $SSH "cd $BASE && ./start-namenode.sh";
}

if [[ $# != 4 ]]; then
    usage;
else
    # always run prepare
    while read -r line;
    do
        prepare $2 $line $4;
    done < $3;

    # boot up hdfs
    case $1 in
        "namenode")
            IP_LINE=$(cat $3 | head -n1);
            start_namenode $2 $IP_LINE $4;
            ;;
        "datanode")
            while read -r line; do start_datanode $2 $line $4; done < $3;
            ;;
        "*")
            usage;
            ;;
    esac;
fi
