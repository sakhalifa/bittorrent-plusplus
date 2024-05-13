#include "command.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct file **announce(
    struct announce arg, struct file **files, int *nb_file, struct peer *peer) {
	peer->port = arg.port;
	for (int i = 0; i < arg.nb_file; i++) {
		files = add_seed(arg.file_list[i], files, nb_file, peer);
	}
	for (int i = 0; i < arg.nb_key; i++) {
		files = add_leech(arg.key_list[i], files, nb_file, peer);
	}
	return files;
}

char *getfile(
    struct getfile arg, struct file **files, int *nb_file, struct peer *peer) {
	struct file *file = seek_filename(arg.key, files, nb_file);
	if (file == NULL) {
		char *r = malloc(sizeof(char) * (strlen("peers")) + 1);
		strcpy(r, "peers");
		return r;
	}
	int length = 9 + strlen(arg.key);
	for (int i = 0; i < file->nb_peers; i++) {
		length += strlen(file->peers[i]->ip) +
		    snprintf(NULL, 0, "%d", file->peers[i]->port) + 1;
		if (i != file->nb_peers - 1) {
			length++;
		}
	}
	char *res = malloc(sizeof(char) * (length + 1));

	res[0] = '\0';
	strcat(res, "peers ");
	strcat(res, arg.key);
	strcat(res, " [");
	for (int i = 0; i < file->nb_peers; i++) {
		strcat(res, file->peers[i]->ip);
		strcat(res, ":");
		int str_length = snprintf(NULL, 0, "%d", file->peers[i]->port);
		char *str      = malloc(sizeof(char) * (str_length + 1));
		snprintf(str, str_length + 1, "%d", file->peers[i]->port);
		strcat(res, str);
		free(str);
		if (i != file->nb_peers - 1) {
			strcat(res, " ");
		}
	}
	strcat(res, "]");

	return res;
}

struct file **update(
    struct update arg, struct file **files, int *nb_file, struct peer *peer) {
	int i;
	for (i = 0; i < arg.nb_key; i++) {
		files = add_leech(arg.key_list[i], files, nb_file, peer);
	}
	return files;
}

char *look(
    struct look arg, struct file **files, int *nb_file, struct peer *peer) {
	struct file **res = NULL;
	int size_res      = 0;

	if (arg.nb_criteria != 0) {
		for (int i = 0; i < *nb_file; i++) {
			int passed = 0;
			for (int j = 0; j < arg.nb_criteria; j++) {
				passed += check_criteria(arg.criteria[j], files[i]);
			}
			if (passed == arg.nb_criteria) {
				if (size_res == 0) {
					res = malloc(sizeof(struct file *));
				} else {
					res = realloc(res, sizeof(struct file *) * (size_res + 1));
				}
				res[size_res] = create_file(files[i]->name, files[i]->filesize,
				    files[i]->piecesize, files[i]->key);
				size_res++;
			}
		}
	}

	char str_length = 7;
	char *str       = malloc(sizeof(char) * str_length);
	strcpy(str, "list [");
	for (int i = 0; i < size_res; i++) {
		char *f = file_to_string(res[i]);
		str_length += strlen(f) + 1;
		str = realloc(str, sizeof(char) * (str_length + 1));
		strcat(str, f);
		if (i + 1 < size_res) {
			strcat(str, " ");
		}

		free(f);
		free_file(res[i]);
	}
	strcat(str, "]");
	free(res);
	return str;
}