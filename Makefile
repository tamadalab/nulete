VERSION := 0.1.0

nulete: cmd/assets/gentests.jar main.go cmd/*.go cmd/assets/*
	go build -o nulete main.go

cmd/assets/gentests.jar: gentests/build.gradle gentests/src/main/java/*.java
	gradle -p gentests shadowJar
	cp gentests/build/libs/gentests-all.jar cmd/assets/gentests.jar

build: nulete

install: build
	sudo cp nulete /usr/local/bin/nulete

uninstall:
	sudo rm -f /usr/local/bin/nulete

clean:
	go clean
	gradle -p gentests clean
	rm -f cmd/assets/gentests.jar

distclean: clean
	rm -rf dist

dist: build
	mkdir -p dist/darwin-amd64/nulete-${VERSION}/bin
	cp nulete dist/darwin-amd64/nulete-${VERSION}/bin
	cp -r doc dist/darwin-amd64/nulete-${VERSION}/
	cp LICENSE README.md dist/darwin-amd64/nulete-${VERSION}/
	tar cvfz dist/nulete-${VERSION}-darwin-amd64.tar.gz -C dist/darwin-amd64 nulete-${VERSION}
