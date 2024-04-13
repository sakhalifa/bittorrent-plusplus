#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "file.h"

int add_seed(char *name, int filesize, int piecesize, char *key,
    struct file files[], int *size, struct peer *peer) {
	struct file *file = seek_filename(key, files, size);
	if (file == NULL) {
		*size += 1;
		files                 = realloc(files, sizeof(struct file) * *size);
		struct file *new_file = malloc(sizeof(struct file));
		new_file->name        = strdup(name);
		new_file->key         = strdup(key);
		new_file->nb_peers    = 1;
		new_file->piecesize   = piecesize;
		new_file->filesize    = filesize;
		new_file->peers       = malloc(sizeof(struct peer));
		new_file->peers[0]    = *peer;
		files[*size - 1]      = *new_file;
		return 1;
	}

	// check if file already existing has same information
	if (strcmp(file->name, name) == 0 || file->filesize != filesize ||
	    file->piecesize != piecesize) {
		return -1;
	}

	file->nb_peers++;
	file->peers = realloc(file->peers, sizeof(struct peer) * file->nb_peers);
	file->peers[file->nb_peers - 1] = *peer;
	return 1;
}

int add_leech(char *key, struct file files[], int *size, struct peer *peer) {
	struct file *file = seek_filename(key, files, size);
	if (file == NULL) {
		return -1; // File not found
	}
	file->nb_peers++;
	file->peers = realloc(file->peers, sizeof(struct peer) * file->nb_peers);
	file->peers[file->nb_peers - 1] = *peer;
	return 1;
}

int rm_file(struct file file, struct file files[], int *size) {
	return -1;
}

struct file *seek_filename(char *key, struct file files[], int *size) {
	for (unsigned int i = 0; i < *size; i++) {
		if (strcmp(files[i].key, key) == 0) {
			return &(files[i]);
		}
	}
	return NULL;
}
