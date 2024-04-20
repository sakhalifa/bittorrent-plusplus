CC = gcc
CFLAGS = -Wall -Isrc/ -g
SRC_DIR = src
TST_DIR = tst


all: tracker

%.o: $(SRC_DIR)/%.c 
	$(CC) $(CFLAGS) $< -c

%.o: $(TST_DIR)/%.c 
	$(CC) $(CFLAGS) $< -c

tracker: tracker.o parser.o command.o file.o
	${CC} $^ -o $@

test: tst_main.o tst_parser.o tst_command.o parser.o file.o command.o peer.o
	$(CC) $(CFLAGS) $^ -o tst_main
	./tst_main

clean:
	rm -f *.o tst_main
