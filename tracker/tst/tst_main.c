#include <stdio.h>
#include <stdlib.h>

#include "tst.h"

int main() {

	// Test parser.c
    printf("TEST PARSER\n");
	test_parser_getfile();
	test_parser_announce();
	test_parser_look();
	test_parser_update();

	return 0;
}