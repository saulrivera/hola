DIR_NAME :=

build:
	mvn clean package

create_docker_image:
	docker build -t outland/emr/tracing .

run_docker_compose:
	docker-compose up --build

build_and_run_compose:
	mvn clean package && docker-compose up --build
