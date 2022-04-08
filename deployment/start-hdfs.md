# Building and boot up hadoop by docker-compose

(2022-03-31)
author: larycoder <lenhuchuhiep99@gmail.com>

We following [link](https://shortcut.com/developer-how-to/how-to-set-up-a-hadoop-cluster-in-docker)
for building and running hadoop cluster in docker. This tutorial depends on
big-data-europe image as base hadoop images. However, for customization, the
image is cloned and modified which, at this moment, is stored in **larycoder**
github (mentioned below).

## Requirement

1. docker engine
2. docker compose

## Setup

1. Clone hadoop image from customized repo:

```
git clone https://github.com/larycoder/docker-hadoop.git
```

2. Setup volumes at the end line of file **docker-compose.yml**:

```
volumes:
    hadoop_namenode: $NAMENODE_STORAGE
    hadoop_datanode: $DATANODE_STORAGE
    hadoop_historyserver: $HISTORYSERVER_STORAGE
```

3. Boots up docker by docker compose:

```
docker-compose up -d
```

