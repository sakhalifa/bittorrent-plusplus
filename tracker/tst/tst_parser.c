#include "parser.h"
#include "tst.h"
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void test_parser_getfile() {
	printf("\t%s", __func__);

	char command[] = "getfile xq8546qzDA64";

	struct command *parsed = parsing(command);
	assert(parsed != NULL);
	assert(parsed->command_name == GETFILE);

	struct getfile *arg = (struct getfile *)parsed->command_arg;
	assert(strcmp(arg->key, "xq8546qzDA64") == 0);

	free(arg);
	free(parsed);
	printf("\tOK\n");
}

void test_parser_announce() {
	printf("\t%s", __func__);

	char command[] = "announce listen 1005 seed [ImTheName 1024 256 ImTheKey] "
	                 "leech [Key2 key3]";

	struct command *parsed = parsing(command);
	assert(parsed != NULL);
	assert(parsed->command_name == ANNOUNCE);

	struct announce *arg = (struct announce *)parsed->command_arg;
	assert(arg->nb_file == 1);
	assert(arg->nb_key == 2);
	assert(strcmp(arg->key_list[0], "Key2") == 0);
	assert(strcmp(arg->key_list[1], "key3") == 0);
	assert(arg->file_list[0].filesize == 1024);
	assert(arg->file_list[0].piecesize == 256);
	assert(strcmp(arg->file_list[0].key, "ImTheKey") == 0);
	assert(strcmp(arg->file_list[0].name, "ImTheName") == 0);

	for (int i = 0; i < arg->nb_key; i++) {
		free(arg->key_list[i]);
	}
    for (int i = 0; i < arg->nb_file; i++) {
        free(arg->file_list[i].key);
        free(arg->file_list[i].peers);
    }
	free(arg->file_list);
	free(arg->key_list);
	free(arg);
	free(parsed);

	printf("\tOK\n");
}

void test_parser_look() {
	printf("\t%s", __func__);

	char command[] = "look [filename=\"ImTheD\" piecesize<\"256\"]";

	struct command *parsed = parsing(command);
	assert(parsed != NULL);
	assert(parsed->command_name == LOOK);

	struct look *arg = (struct look *)parsed->command_arg;
	assert(arg->nb_criteria == 2);
	assert(arg->criteria[0]->comp == EQ);
	assert(strcmp(arg->criteria[0]->element, "filename") == 0);
	assert(strcmp(arg->criteria[0]->value, "ImTheD") == 0);
	assert(arg->criteria[1]->comp == LT);
	assert(strcmp(arg->criteria[1]->element, "piecesize") == 0);
	assert(strcmp(arg->criteria[1]->value, "256") == 0);

	for (int i = 0; i < arg->nb_criteria; i++) {
		free(arg->criteria[i]);
	}
	free(arg->criteria);
	free(arg);
	free(parsed);

	printf("\tOK\n");
}

void test_parser_update() {
	printf("\t%s", __func__);

	char command[] = "update seed [MeKey1 MeKey2] leech [MeKey3 MeKey4]";

	struct command *parsed = parsing(command);
	assert(parsed != NULL);
	assert(parsed->command_name == UPDATE);

	struct update *arg = (struct update *)parsed->command_arg;
	assert(arg->nb_key == 4);
	assert(strcmp(arg->key_list[0], "MeKey1") == 0);
	assert(strcmp(arg->key_list[1], "MeKey2") == 0);
	assert(strcmp(arg->key_list[2], "MeKey3") == 0);
	assert(strcmp(arg->key_list[3], "MeKey4") == 0);

	for (int i = 0; i < arg->nb_key; i++) {
		free(arg->key_list[i]);
	}
	free(arg->key_list);
	free(arg);
	free(parsed);

	printf("\tOK\n");
}