# ULake

This is the USTH Data Lake.


# Boot it up

To build everything:

```bash
./gradlew quarkusBuild
```

To start the lake services in dev mode:

```bash
./gradlew dev -Ddebug=false
```

To build native binaries and start all lake services:

```bash
./gradlew build -Dquarkus.package.type=native
```


# Hacking

We use Intellij IDEA as the main IDE. Simply open this directory in IDEA and start the available sub-projects as services. 
