CC = gcc
CFLAGS = -Wall

TST_DIR = tst
SRC_DIR = src





parser.o: ${SRC_DIR}/parser.c ${SRC_DIR}/parser.h
	${COMPILE.c} $< -o $@


tracker.o: ${SRC_DIR}/tracker.c
	${COMPILE.c} $< -o $@


clean:
	rm *.o tracker