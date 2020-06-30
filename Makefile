.PHONY: build test clean

all: localhost-clean jar-clean jar-build docker-build localhost-start

STAGE=dev
VERSION=$(shell cat build.gradle | grep 'version =' | cut -d'=' -f2 | tr -d "'" | tr -d " ")
JAR_FILE=build/libs/ecs-example-${VERSION}.jar

build: jar-build docker-build
test: jar-test
clean: localhost-clean jar-clean

jar-clean:
	./gradlew clean

jar-test:
	./gradlew test

jar-build:
	./gradlew assemble

docker-build: jar-build
	docker build --rm -t '${STAGE}/aws-ecs-example' --build-arg JAR_FILE=${JAR_FILE} -f \
		./docker/image/Dockerfile .

docker-clean:
	docker rmi -f ${STAGE}/aws-ecs-example

localhost-start:
	docker-compose -f docker/localhost.yaml up -d

localhost-stop:
	docker-compose -f docker/localhost.yaml down

localhost-clean:
	docker-compose -f docker/localhost.yaml down -v

ecr-publish: jar-build docker-build
	aws ecr get-login-password --region eu-west-1 | docker login --username AWS \
		--password-stdin 080445063818.dkr.ecr.eu-west-1.amazonaws.com

	docker tag ${STAGE}/aws-ecs-example:latest \
		080445063818.dkr.ecr.eu-west-1.amazonaws.com/${STAGE}/aws-ecs-example:latest

	docker tag ${STAGE}/aws-ecs-example:latest \
		080445063818.dkr.ecr.eu-west-1.amazonaws.com/${STAGE}/aws-ecs-example:${VERSION}

	docker push 080445063818.dkr.ecr.eu-west-1.amazonaws.com/${STAGE}/aws-ecs-example:latest
	docker push 080445063818.dkr.ecr.eu-west-1.amazonaws.com/${STAGE}/aws-ecs-example:${VERSION}
