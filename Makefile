nulete: cmd/assets/gentests.jar main.go cmd/*.go cmd/assets/*
	go build -o nulete main.go

cmd/assets/gentests.jar: gentests/build.gradle gentests/src/main/java/GenerateTest.java
	gradle -p gentests jar
	cp gentests/build/libs/gentests.jar cmd/assets/gentests.jar

build: nulete

clean:
	go clean
	gradle -p gentests clean
	rm cmd/assets/gentests.jar
