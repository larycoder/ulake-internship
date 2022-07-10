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

# Boot it up

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

We use nginx as reverse proxy for the whole ulake. To start nginx in a docker container:

```bash
./deployment/start-nginx.sh
```

# Hacking

We use Intellij IDEA/Visual Studio Code/vim as the main IDEs. Simply open this directory in your favorite IDE and start the available sub-projects as services.
