artifact_name       := order-notification-sender
version             := latest
repository_prefix   := 416670754337.dkr.ecr.eu-west-2.amazonaws.com
dependency_check_base_suppressions:=common_suppressions_spring_6.xml

# dependency_check_suppressions_repo_branch
# The branch of the dependency-check-suppressions repository to use
# as the source of the suppressions file.
# This should point to "main" branch when being used for release,
# but can point to a different branch for experimentation/development.
dependency_check_suppressions_repo_branch:=feature/suppressions-for-company-accounts-api

dependency_check_minimum_cvss := 4
dependency_check_assembly_analyzer_enabled := false
dependency_check_suppressions_repo_url:=git@github.com:companieshouse/dependency-check-suppressions.git
suppressions_file := target/suppressions.xml

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
package:
ifndef version
	$(error No version given. Aborting)
endif
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	$(info Packaging version: $(version))
	@test -s ./$(artifact_name).jar || { echo "ERROR: Service JAR not found"; exit 1; }
	$(eval tmpdir:=$(shell mktemp -d build-XXXXXXXXXX))
	cp ./$(artifact_name).jar $(tmpdir)/$(artifact_name).jar
	cd $(tmpdir); zip -r ../$(artifact_name)-$(version).zip *
	rm -rf $(tmpdir)

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

.PHONY: dependency-check
dependency-check:
	@ if [ -d "$(DEPENDENCY_CHECK_SUPPRESSIONS_HOME)" ]; then \
		suppressions_home="$${DEPENDENCY_CHECK_SUPPRESSIONS_HOME}"; \
	fi; \
	if [ ! -d "$${suppressions_home}" ]; then \
	    suppressions_home_target_dir="./target/dependency-check-suppressions"; \
		if [ -d "$${suppressions_home_target_dir}" ]; then \
			suppressions_home="$${suppressions_home_target_dir}"; \
		else \
			mkdir -p "./target"; \
			git clone $(dependency_check_suppressions_repo_url) "$${suppressions_home_target_dir}" && \
				suppressions_home="$${suppressions_home_target_dir}"; \
			if [ -d "$${suppressions_home_target_dir}" ] && [ -n "$(dependency_check_suppressions_repo_branch)" ]; then \
				cd "$${suppressions_home}"; \
				git checkout $(dependency_check_suppressions_repo_branch); \
				cd -; \
			fi; \
		fi; \
	fi; \
	suppressions_path="$${suppressions_home}/suppressions/$(dependency_check_base_suppressions)"; \
	if [  -f "$${suppressions_path}" ]; then \
		cp -av "$${suppressions_path}" $(suppressions_file); \
		mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=$(dependency_check_minimum_cvss) -DassemblyAnalyzerEnabled=$(dependency_check_assembly_analyzer_enabled) -DsuppressionFiles=$(suppressions_file); \
	else \
		printf -- "\n ERROR Cannot find suppressions file at '%s'\n" "$${suppressions_path}" >&2; \
		exit 1; \
	fi

.PHONY: security-check
security-check: dependency-check