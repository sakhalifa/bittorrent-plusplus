#ifndef TST_H
#define TST_H

#include <stdio.h>

// FILE
void test_file_file_to_string();
void test_look_criteria();

// PARSER
void test_parser_announce();
void test_parser_getfile();
void test_parser_look();
void test_parser_update();


// COMMAND
void test_command_announce();
void test_command_look();
void test_command_getfile();
void test_command_update();


// THREAD POOL
void test_task_queue_init();
void test_task_queue_push();
void test_task_queue_pull();
void test_task_queue_clear();
void test_thpool_init();
void test_thpool_add_work();

#endif
