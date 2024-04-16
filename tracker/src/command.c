#include "command.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char *announce(
    struct announce arg, struct file files[], int *nb_file, struct peer *peer) {
	for (int i = 0; i < arg.nb_file; i++) {
		add_seed(arg.file_list[i].name, arg.file_list[i].filesize,
		    arg.file_list[i].piecesize, arg.file_list[i].key, files, nb_file,
		    peer);
	}
	for (int i = 0; i < arg.nb_key; i++) {
		add_leech(arg.key_list[i], files, nb_file, peer);
	}

	return "ok";
}

char *getfile(
    struct getfile arg, struct file files[], int *nb_file, struct peer *peer) {
	struct file *file = seek_filename(arg.key, files, nb_file);
	int length        = 9 + strlen(arg.key);
	for (int i = 0; i < file->nb_peers; i++) {
		length += strlen(file->peers[i].ip) +
		    snprintf(NULL, 0, "%d", file->peers[i].port) + 1;
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
		strcat(res, file->peers[i].ip);
		strcat(res, ":");
		int str_length = snprintf(NULL, 0, "%d", file->peers[i].port);
		char *str      = malloc(sizeof(char) * (str_length + 1));
		snprintf(str, str_length + 1, "%d", file->peers[i].port);
		strcat(res, str);
		free(str);
		if (i != file->nb_peers - 1) {
			strcat(res, " ");
		}
	}
	strcat(res, "]");

	return res;
}