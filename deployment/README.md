# Deploying ulake system (2022-03-28)

Document version: 0.2.0

## Deployment components

1. WebApp (nginx)                   port: 8080
2. Core Service (API)               port: 8784
3. Folder Service (API)             port: 8786
4. User Service (API)               port: 8785
5. Acl Service (API)                port: 8783
6. MySql (docker)                   port: 23306
7. OpenIO (docker)                  port: 6006 -- deprecated
8. Phpmyadmin (docker)              port: 8081
9. Dashboard Service (API)          port: 8782
10. Search Service (API)            port: 8787

8. Hadoop datanode (docker)
9. Hadoop namenode (docker)         port: 9000, 9870
10. Hadoop resourcemanager (docker)
11. Hadoop nodemanager (docker)
12. Hadoop historyserver (docker)

## Strategy

1. WebApp: setup script
2. Service: screen
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
