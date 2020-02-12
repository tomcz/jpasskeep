.PHONY: build-jar build-dmg clean test deps

GIT_SHA ?= $(shell git rev-parse --short=7 HEAD)

build-jar:
	./gradlew --console plain -Pversion=${GIT_SHA} clean test shadowJar

build-dmg:
	./gradlew --console plain -Pversion=${GIT_SHA} clean test createDmg

clean:
	./gradlew clean

test:
	./gradlew test

deps:
	./gradlew dependencies
