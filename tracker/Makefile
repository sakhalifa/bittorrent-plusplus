CC = gcc
CFLAGS = -Wall -Isrc/
SRC_DIR = src

TST_DIR = tst


install: parser.o


parser.o: ${SRC_DIR}/parser.c ${SRC_DIR}/parser.h ${SRC_DIR}/command.h ${SRC_DIR}/file.h
	${COMPILE.c} $< -o $@ 

tracker.o: ${SRC_DIR}/tracker.c
	${COMPILE.c} $< -o $@


test: tst_main.o tst_parser.o parser.o
	$(CC) $(CFLAGS) $^ -o $@
	./test

tst_main.o: ${TST_DIR}/tst_main.c ${TST_DIR}/tst.h tst_parser.o
	$(CC) $(CFLAGS) -o $@ -c $<

tst_parser.o: ${TST_DIR}/tst_parser.c ${TST_DIR}/tst.h parser.o
	$(CC) $(CFLAGS) -o $@ -c $<


clean:
	rm -f *.o test
