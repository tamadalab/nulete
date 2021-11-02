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
	rm cmd/assets/gentests.jar
