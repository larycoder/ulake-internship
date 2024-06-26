# ULake

This is the USTH Data Lake.

# Building

To build everything:

```bash
./gradlew build
```

To build a specific service:

```bash
./gradlew <service>:build
```

To build everything native binaries:

```bash
./gradlew build-native
```

# Booting

To start all lake services in dev mode:

```bash
./gradlew dev -Ddebug=false
```

To start all lakes services with native binaries in docker containers:

```bash
./deployment/ulakectl.sh start
```

To stop lakes services:

```bash
./deployment/ulakectl.sh kill
```

# Deployment

All docker containers are conntected using a docker network named ```ulake-network```. HDFS nodes are managed by a global docker service ```ulake-hadoop_ulake-Hdatanode```.

Almost every ulake service depends on a relational database. A MariaDB instance **must** be available in ```ulake-network```. We prepared a MySQL instance as in ```deployment/start-mysql.sh```. Launch the instance and create tables for services.

Finally, we use ```nginx``` as reverse proxy for the whole ulake. To start nginx in a docker container:

```bash
./deployment/start-nginx.sh
```


# Hacking

We use Intellij IDEA/Visual Studio Code/vim as the main IDEs. Simply open this directory in your favorite IDE and start the available sub-projects as services.
