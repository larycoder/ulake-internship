# Service image build guideline

For building ulake service, there are 2 steps:

1. Preparing **ulake.tar** file to load necessary configuration file

```
./build_ulake_dependencies.sh
```

2. Building image by Dockerfile

```
docker build -t ulake/service:$(VERSION) .
```
