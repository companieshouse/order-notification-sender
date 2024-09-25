artifact_name       := order-notification-sender
version             := latest
repository_prefix   := 169942020521.dkr.ecr.eu-west-1.amazonaws.com

.PHONY: all
all: build

.PHONY: clean
clean:
	mvn clean
	rm -f ./$(artifact_name).jar
	rm -f ./$(artifact_name)-*.zip
	rm -rf ./build-*
	rm -f ./build.log

.PHONY: build
build:
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true
	cp ./target/$(artifact_name)-$(version).jar ./$(artifact_name).jar

.PHONY: test
test: test-unit

.PHONY: test-unit
test-unit: clean
	mvn test

.PHONY: package
package: build

.PHONY: dist
dist: clean build docker-build

.PHONY: sonar
sonar:
	mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922:sonar

.PHONY: sonar-pr-analysis
sonar-pr-analysis:
	mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922:sonar -P sonar-pr-analysis

.PHONY: docker-build
docker-build: build
	DOCKER_BUILDKIT=0 docker build -t $(repository_prefix)/$(artifact_name):$(version) .
