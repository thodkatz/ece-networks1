SRC = src/*.java

$(shell mkdir -p classes logs media)
CLASSES = classes
MAIN    = src.UserApplication

all: build run

build: $(SRC)
	javac -cp .:lib/* -d $(CLASSES) $^

run:
	@java -cp $(CLASSES):lib/* $(MAIN)

.PHONY: clean

clean:
	rm -rf $(CLASSES)/*

clean_logs:
	rm -f media/*.jpg logs/*.txt