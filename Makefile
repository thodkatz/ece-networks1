SRC = src/*.java

$(shell mkdir -p classes)
CLASSES = classes
MAIN    = src.UserApplication

all: build run

build: $(SRC)
	javac -cp lib/ithakimodem.jar -d $(CLASSES) $^

run:
	@java -cp $(CLASSES):lib/ithakimodem.jar $(MAIN)

.PHONY: clean

clean:
	rm -rf $(CLASSES)/*