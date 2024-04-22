#include "file.h"
#include "tst.h"
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void test_file_file_to_string() {
	printf("\t%s", __func__);

	struct file *f = create_file("Name", 555, 12, "Key69696969");

	char *str = file_to_string(f);

	assert(strcmp(str, "Name 555 12 Key69696969") == 0);

	free(str);
	free_file(f);

	printf("\tOK\n");
}

void test_look_criteria() {
	printf("\t%s", __func__);

	int nb_files        = 4;
	struct file **files = malloc(sizeof(struct file *) * nb_files);
	files[0]            = create_file("Name1", 1024, 256, "Key1");
	files[1]            = create_file("Name2", 2048, 32, "Key2");
	files[2]            = create_file("Name3", 2048, 16, "Key3");
	files[3]            = create_file("Name4", 7171, 727, "Key7");

	struct criteria *crit = malloc(sizeof(struct criteria));
	crit->comp            = EQ;
	crit->element         = malloc(sizeof(char) * (strlen("filesize") + 1));
	strcpy(crit->element, "filesize");
	crit->value = malloc(sizeof(char) * (strlen("2048") + 1));
	strcpy(crit->value, "2048");

	int size_res      = 0;
	struct file **res = NULL;

	res = look_criteria(crit, files, &nb_files, res, &size_res);

    assert(size_res == 2);
	assert(res[0]->filesize == 2048);
	assert(res[1]->filesize == 2048);
	assert(res[0]->piecesize == 32);
	assert(strcmp(res[0]->name, "Name2") == 0);
	assert(strcmp(res[0]->key, "Key2") == 0);
	assert(res[1]->piecesize == 16);
	assert(strcmp(res[1]->name, "Name3") == 0);
	assert(strcmp(res[1]->key, "Key3") == 0);

	for (int i = 0; i < size_res; i++) {
		free_file(res[i]);
	}
	free(res);


    struct criteria * crit2 = malloc(sizeof(struct criteria));
    crit2->comp = GT;
    crit2->element = malloc(sizeof(char) * (strlen("filesize") + 1));
    strcpy(crit2->element, "filesize");
    crit2->value = malloc(sizeof(char) * (strlen("2048") + 1));
    strcpy(crit2->value, "2048");

    int size_res2 = 0;
    struct file ** res2 = NULL;

	res2 = look_criteria(crit2, files, &nb_files, res2, &size_res2);

    assert(size_res2 == 1);
    assert(res2[0]->filesize == 7171);
    assert(res2[0]->piecesize == 727);
    assert(strcmp(res2[0]->key, "Key7") == 0);
    assert(strcmp(res2[0]->name, "Name4") == 0);

	for (int i = 0; i < size_res2; i++) {
		free_file(res2[i]);
	}
	free(res2);

	for (int i = 0; i < nb_files; i++) {
		free_file(files[i]);
	}
	free(files);
	free_criteria(crit);

	printf("\tOK\n");
}