CC = gcc
CFLAGS = -Wall -Isrc/
SRC_DIR = src
TST_DIR = tst


all: tracker

%.o: $(SRC_DIR)/%.c 
	$(CC) $(CFLAGS) $< -c

%.o: $(TST_DIR)/%.c 
	$(CC) $(CFLAGS) $< -c

tracker: tracker.o parser.o
	${CC} tracker.o parser.o -o $@

test: tst_main.o tst_parser.o parser.o
	$(CC) $(CFLAGS) $^ -o $@
	./test

clean:
	rm *.o test