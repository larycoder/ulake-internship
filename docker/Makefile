VERSION = "1.0.0-SNAPSHOT"

build:
	./service/build_ulake_dependencies.sh
	docker build -t ulake/service:$(VERSION) ./service
