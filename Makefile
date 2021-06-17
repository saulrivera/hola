VERSION := 0.0.6

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

test_docker:
	docker run --network="host" -e mongoPassword=root -e mongoHost=127.0.0.1 -e redisHost=127.0.0.1 -e neo4jHost=bolt://127.0.0.1:7687 -e neo4jUser=neo4j -e neo4jPass=s3cr3t 994649651276.dkr.ecr.us-east-1.amazonaws.com/emrtks:$(VERSION)
