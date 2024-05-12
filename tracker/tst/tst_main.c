#include <stdio.h>
#include <stdlib.h>

#include "tst.h"

int main() {

	// Test file.c
    printf("TEST FILE\n");
	test_file_file_to_string();
	test_look_criteria();

	// Test parser.c
    printf("TEST PARSER\n");
	test_parser_getfile();
	test_parser_announce();
	test_parser_look();
	test_parser_update();

	// Test command.c
    printf("TEST COMMAND\n");
	// test_command_announce();
	// test_command_getfile();
	// test_command_look();
	// test_command_update();

	// Test thpool.c
	printf("TEST THREAD POOL\n");
	test_task_queue_init();
	test_task_queue_push();
	test_task_queue_pull();
	test_task_queue_clear();
	test_thpool_init();

	return 0;
}