# Deploying ulake system (2022-03-28)

Document version: 0.2.0

## Deployment components

### Interface

1. WebApp (nginx)                   port: 8080

### Service

1. Core Service (API)                          port: 8784
2. Folder Service (API)                        port: 8786
3. User Service (API)                          port: 8785
4. Acl Service (API)                           port: 8783
7. Search Service (API)                        port: 8787
6. Dashboard Service (API)                     port: 8782
7. Admin Service (API)                         port: 8781
8. Tabular Data Service (API)                  port: 8788
9. Ingestion Service (API)                     port: 8789
10. Logging Service (API)                      port: 8790
10. Compression Service (API)                  port: 8791
11. Indexing and Retrieval Service (API)       port: 8792

### Storage

1. MySql (docker)                   port: 23306 swarm_port: 3306
2. OpenIO (docker)                  port: 6006 -- deprecated
3. Hadoop namenode (docker)
4. Hadoop datanode (swarm)

### Tool

1. Phpmyadmin (docker)               port: 8081 swarm_port: 80

## Strategy

1. WebApp: setup script
2. Service: dockerfile
3. Docker: setup script

## Nginx

Since nginx setup is pretty complex part, there is a section for setup once.

### Role

1. Route path to service
2. Dashboard holder
3. Https

### Setup

1. Start script
2. Configuration file

### Name-based virtual host

1. User service: user.ulake.sontg.net
2. Folder service: folder.ulake.sontg.net
3. Core service: core.ulake.sontg.net

## Note

1. Update service resource to connect to proper docker host
2. Prepare proper java-environment (openjdk-11)
3. Setup port in application properties for each services
4. Update start-phpmyadmin.sh port following start-mysql.sh
5. Nginx in docker need to link to Machine host
6. Need to map domain name to IP for activating service vhost
7. Hadoop system will be updated manually basing on big-data-europe work
8. Hadoop will run on cluster base on docker swarm
9. Services will run on docker build from dockerfile and mount volume to outside
10. Services, Hadoop and Mysql will share same network
