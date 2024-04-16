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
		files[*size - 1].name = strdup(name);
		files[*size - 1].key         = strdup(key);
		files[*size - 1].nb_peers    = 1;
		files[*size - 1].piecesize   = piecesize;
		files[*size - 1].filesize    = filesize;
		files[*size - 1].peers       = malloc(sizeof(struct peer));
		files[*size - 1].peers[0]    = *peer;
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
