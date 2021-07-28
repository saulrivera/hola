VERSION := 0.7.0

build:
	mvn clean package

create_docker_image:
	mvn clean package &&\
	docker build -t 994649651276.dkr.ecr.us-east-1.amazonaws.com/emrtks:$(VERSION) .

run_docker_compose:
	docker-compose up --build

build_and_run_compose:
	mvn clean package && docker-compose up --build

deploy:
	mvn clean package &&\
	aws ecr get-login-password --region us-east-1 --profile emr | docker login --username AWS --password-stdin 994649651276.dkr.ecr.us-east-1.amazonaws.com &&\
	docker build -t 994649651276.dkr.ecr.us-east-1.amazonaws.com/emrtks:$(VERSION) . &&\
	docker push 994649651276.dkr.ecr.us-east-1.amazonaws.com/emrtks:$(VERSION)
