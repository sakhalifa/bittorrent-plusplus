#include "command.h"
#include "tst.h"
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void test_command_announce() {
	printf("\t%s", __func__);

	int nb_files        = 2;
	struct file **files = malloc(sizeof(struct file *) * nb_files);
	files[0]            = create_file("Name1", 1024, 256, "Key1");
	files[0]->nb_peers  = 2;
	files[0]->peers     = malloc(sizeof(struct peer *) * files[0]->nb_peers);
	files[0]->peers[0]  = create_peer("5.5.5.5", 5555);
	files[0]->peers[1]  = create_peer("0.0.0.0", 6969);
	files[1]            = create_file("Name2", 2048, 32, "Key2");
	files[1]->nb_peers  = 1;
	files[1]->peers     = malloc(sizeof(struct peer *) * files[1]->nb_peers);
	files[1]->peers[0]  = create_peer("0.0.0.0", 6969);

	struct peer *p3 = create_peer("je suis une ip", 1234);

	struct announce *arg = malloc(sizeof(struct announce));
	arg->port            = 1234;
	arg->nb_file         = 2;
	arg->file_list       = malloc(sizeof(struct file *) * arg->nb_file);
	arg->file_list[0]    = create_file("Name1", 1024, 256, "Key1");
	arg->file_list[1]    = create_file("Name3", 4096, 64, "Key3");
	arg->nb_key          = 1;
	arg->key_list        = malloc(sizeof(char *) * arg->nb_key);
	arg->key_list[0]     = malloc(sizeof(char) * (strlen("Key2") + 1));
	arg->key_list[0]     = strcpy(arg->key_list[0], "Key2");

	char *res = announce(*arg, files, &nb_files, p3);

	assert(strcmp(res, "ok") == 0);
	assert(p3->port == 1234);
	assert(nb_files == 3);
	assert(files[0]->nb_peers == 3);
	assert(files[0]->peers[2]->port == 1234);
	assert(strcmp(files[0]->peers[2]->ip, "je suis une ip") == 0);
	assert(files[2]->filesize == 4096);
	assert(files[2]->nb_peers == 1);
	assert(files[2]->peers[0]->port == 1234);
	assert(strcmp(files[2]->peers[0]->ip, "je suis une ip") == 0);
	assert(files[1]->nb_peers == 2);

	free_peer(p3);
	for (int i = 0; i < arg->nb_file; i++) {
		free_file(arg->file_list[i]);
	}
	free(arg->file_list);
	for (int i = 0; i < arg->nb_key; i++) {
		free(arg->key_list[i]);
	}
	free(arg->key_list);
	free(arg);
	for (int i = 0; i < nb_files; i++) {
		free_file(files[i]);
	}
	free(files);

	printf("\tOK\n");
}

void test_command_getfile() {
	printf("\t%s", __func__);

	int nb_files        = 2;
	struct file **files = malloc(sizeof(struct file *) * nb_files);
	files[0]            = create_file("Name1", 1024, 256, "Key1");
	files[0]->nb_peers  = 2;
	files[0]->peers     = malloc(sizeof(struct peer *) * files[0]->nb_peers);
	files[0]->peers[0]  = create_peer("5.5.5.5", 5555);
	files[0]->peers[1]  = create_peer("0.0.0.0", 6969);
	files[1]            = create_file("Name2", 2048, 32, "Key2");
	files[1]->nb_peers  = 1;
	files[1]->peers     = malloc(sizeof(struct peer *) * files[1]->nb_peers);
	files[1]->peers[0]  = create_peer("0.0.0.0", 6969);

	struct getfile *arg = malloc(sizeof(struct getfile));
	arg->key            = malloc(sizeof(char) * (strlen("Key1") + 1));
	arg->key            = strcpy(arg->key, "Key1");

	struct peer *p3 = create_peer("0.0.0.0", 1234);

	char *res = getfile(*arg, files, &nb_files, p3);

	assert(strcmp(res, "peers Key1 [5.5.5.5:5555 0.0.0.0:6969]") == 0);

	free(res);
	free_peer(p3);
	free(arg->key);
	free(arg);
	for (int i = 0; i < nb_files; i++) {
		free_file(files[i]);
	}
	free(files);

	printf("\tOK\n");
}

void test_command_look() {
	printf("\t%s", __func__);

	int nb_files        = 4;
	struct file **files = malloc(sizeof(struct file *) * nb_files);
	files[0]            = create_file("Name1", 1024, 256, "Key1");
	files[1]            = create_file("Name2", 2048, 32, "Key2");
	files[2]            = create_file("Name3", 2048, 16, "Key3");
	files[3]            = create_file("Name4", 7171, 727, "Key7");

	struct criteria *crit = malloc(sizeof(struct criteria));
	crit->comp            = LT;
	crit->element         = malloc(sizeof(char) * (strlen("filesize") + 1));
	strcpy(crit->element, "filesize");
	crit->value = malloc(sizeof(char) * (strlen("2049") + 1));
	strcpy(crit->value, "2049");

	struct criteria *crit2 = malloc(sizeof(struct criteria));
	crit2->comp            = GT;
	crit2->element         = malloc(sizeof(char) * (strlen("piecesize") + 1));
	strcpy(crit2->element, "piecesize");
	crit2->value = malloc(sizeof(char) * (strlen("16") + 1));
	strcpy(crit2->value, "16");

	struct look *arg = malloc(sizeof(struct look));
	arg->nb_criteria = 2;
	arg->criteria    = malloc(sizeof(struct criteria *) * arg->nb_criteria);
	arg->criteria[0] = crit;
	arg->criteria[1] = crit2;

	struct peer *p = create_peer("pizza", 0000);

	char *res = look(*arg, files, &nb_files, p);

	assert(strcmp(res, "list [Name1 1024 256 Key1 Name2 2048 32 Key2]") == 0);

	free_peer(p);
	free(res);

	for (int i = 0; i < arg->nb_criteria; i++) {
		free_criteria(arg->criteria[i]);
	}
	free(arg->criteria);
	free(arg);

	for (int i = 0; i < nb_files; i++) {
		free_file(files[i]);
	}
	free(files);

	printf("\tOK\n");
}

void test_command_update() {
	printf("\t%s", __func__);

	int nb_files        = 4;
	struct file **files = malloc(sizeof(struct file *) * nb_files);
	files[0]            = create_file("Name1", 1024, 256, "Key1");
	files[1]            = create_file("Name2", 2048, 32, "Key2");
	files[2]            = create_file("Name3", 2048, 16, "Key3");
	files[3]            = create_file("Name4", 7171, 727, "Key7");
	files[0]->nb_peers  = 1;
	files[0]->peers     = malloc(sizeof(struct peer *));
	files[0]->peers[0]  = create_peer("first", 1111);
	files[1]->nb_peers  = 1;
	files[1]->peers     = malloc(sizeof(struct peer *));
	files[1]->peers[0]  = create_peer("first", 1111);
	files[2]->nb_peers  = 1;
	files[2]->peers     = malloc(sizeof(struct peer *));
	files[2]->peers[0]  = create_peer("second", 2222);
	files[3]->nb_peers  = 1;
	files[3]->peers     = malloc(sizeof(struct peer *));
	files[3]->peers[0]  = create_peer("first", 2222);

	struct peer *p = create_peer("pizza", 0000);

	struct update *arg = malloc(sizeof(struct update));
	arg->nb_key        = 2;
	arg->key_list      = malloc(sizeof(char *) * arg->nb_key);
	arg->key_list[0]   = strdup("Key2");
	arg->key_list[1]   = strdup("Key7");

	char *res = update(*arg, files, &nb_files, p);

	assert(strcmp(res, "ok") == 0);
	assert(files[1]->nb_peers == 2);
	assert(files[3]->nb_peers == 2);
	assert(files[0]->nb_peers == 1);
	assert(files[2]->nb_peers == 1);
	assert(files[1]->peers[1]->port == 0000);
	assert(files[3]->peers[1]->port == 0000);
	assert(strcmp(files[1]->peers[1]->ip, "pizza") == 0);
	assert(strcmp(files[3]->peers[1]->ip, "pizza") == 0);

	for (int i = 0; i < arg->nb_key; i++) {
		free(arg->key_list[i]);
	}
	free(arg->key_list);
	free(arg);
	for (int i = 0; i < nb_files; i++) {
		free_file(files[i]);
	}
	free(files);
	free_peer(p);
	printf("\tOK\n");
}