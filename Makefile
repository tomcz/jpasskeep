.PHONY: build-jar build-dmg clean test deps

GIT_SHA ?= $(shell git rev-parse --short=7 HEAD)

build-jar: clean
	./gradlew --console plain -Pversion=${GIT_SHA} test shadowJar

build-dmg: clean
	./gradlew --console plain -Pversion=${GIT_SHA} test createDmg

clean:
	./gradlew --console plain clean

test:
	./gradlew --console plain test

deps:
	./gradlew dependencies
