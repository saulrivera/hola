VERSION := 0.0.4

build:
	mvn clean package

create_docker_image:
	docker build -t outland/emr/tracing .

run_docker_compose:
	docker-compose up --build

build_and_run_compose:
	mvn clean package && docker-compose up --build

deploy:
	mvn clean package &&\
	docker build -t ghcr.io/outlandhq/emr/trackingsystem:$(VERSION) . &&\
	docker push ghcr.io/outlandhq/emr/trackingsystem:$(VERSION)
