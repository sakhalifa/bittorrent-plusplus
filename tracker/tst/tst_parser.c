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

	free(arg->key);
	free(arg);
	free(parsed);
	printf("\tOK\n");
}

void test_parser_announce() {
	printf("\t%s", __func__);

	char command[] = "announce listen 1005 seed [ImTheVeryLongName 1024 256 "
	                 "ImTheKey] leech [Key2 key3]";

	struct command *parsed = parsing(command);
	assert(parsed != NULL);
	assert(parsed->command_name == ANNOUNCE);

	struct announce *arg = (struct announce *)parsed->command_arg;
	assert(arg->nb_file == 1);
	assert(arg->nb_key == 2);
	assert(strcmp(arg->key_list[0], "Key2") == 0);
	assert(strcmp(arg->key_list[1], "key3") == 0);
	assert(arg->file_list[0]->filesize == 1024);
	assert(arg->file_list[0]->piecesize == 256);
	assert(strcmp(arg->file_list[0]->key, "ImTheKey") == 0);
	assert(strcmp(arg->file_list[0]->name, "ImTheVeryLongName") == 0);

	for (int i = 0; i < arg->nb_file; i++) {
		free_file(arg->file_list[i]);
	}

	for (int i = 0; i < arg->nb_key; i++) {
		free(arg->key_list[i]);
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
		free_criteria(arg->criteria[i]);
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

	char command2[]         = "update seed [Key1] leech []";
	struct command *parsed2 = parsing(command2);
	struct update *arg2     = (struct update *)parsed2->command_arg;

	assert(arg2->nb_key == 1);
	assert(strcmp(arg2->key_list[0], "Key1") == 0);

	for (int i = 0; i < arg2->nb_key; i++) {
		free(arg2->key_list[i]);
	}

	free(arg2->key_list);
	free(arg2);
	free(parsed2);

	char command3[]         = "update seed [] leech [Key1]";
	struct command *parsed3 = parsing(command3);
	struct update *arg3     = (struct update *)parsed3->command_arg;

	assert(arg3->nb_key == 1);
	assert(strcmp(arg3->key_list[0], "Key1") == 0);

	for (int i = 0; i < arg3->nb_key; i++) {
		free(arg3->key_list[i]);
	}

	free(arg3->key_list);
	free(arg3);
	free(parsed3);


	char command4[]         = "update seed [] leech []";
	struct command *parsed4 = parsing(command4);
	struct update *arg4     = (struct update *)parsed4->command_arg;

	assert(arg4->nb_key == 0);

	free(arg4->key_list);
	free(arg4);
	free(parsed4);

	printf("\tOK\n");
}