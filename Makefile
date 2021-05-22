GIT_SHA ?= $(shell git rev-parse --short=7 HEAD)

.PHONY: build
build: clean
	./gradlew --console plain -Pversion=${GIT_SHA} test shadowJar

.PHONY: clean
clean:
	./gradlew --console plain clean

.PHONY: test
test:
	./gradlew --console plain test

.PHONY: deps
deps:
	./gradlew dependencies
