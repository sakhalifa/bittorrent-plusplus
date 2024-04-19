#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "file.h"

int add_seed(
    struct file *f, struct file **files, int *size, struct peer *peer) {
	struct file *file = seek_filename(f->key, files, size);
	if (file == NULL) {
		*size += 1;
		files                 = realloc(files, sizeof(struct file *) * (*size));
		files[*size - 1] = f;
		f->peers = malloc(sizeof(struct peer *));
		f->peers[0] = peer;
		return 1;
	}

	// check if file already existing has same information
	if (strcmp(file->name, f->name) == 0 || file->filesize != f->filesize ||
	    file->piecesize != f->piecesize) {
		return -1;
	}

	file->nb_peers++;
	file->peers = realloc(file->peers, sizeof(struct peer *) * file->nb_peers);
	file->peers[file->nb_peers - 1] = peer;
	free(f);
	return 1;
}

int add_leech(char *key, struct file **files, int *size, struct peer *peer) {
	struct file *file = seek_filename(key, files, size);
	if (file == NULL) {
		return -1; // File not found
	}

	file->nb_peers++;
	file->peers = realloc(file->peers, sizeof(struct peer *) * file->nb_peers);
	file->peers[file->nb_peers - 1] = peer;
	return 1;
}

struct file *seek_filename(char *key, struct file **files, int *size) {
	for (unsigned int i = 0; i < *size; i++) {
		if (strcmp(files[i]->key, key) == 0) {
			return files[i];
		}
	}
	return NULL;
}

struct file *create_file(char *name, int filesize, int piecesize, char *key) {
	struct file *f = malloc(sizeof(struct file));
	f->name        = malloc(sizeof(char) * (strlen(name) + 1));
	f->name        = strcpy(f->name, name);

	f->filesize  = filesize;
	f->piecesize = piecesize;

	f->key = malloc(sizeof(char) * (strlen(key) + 1));
	f->key = strcpy(f->key, key);

	f->nb_peers = 0;

	return f;
}

void free_file(struct file *file) {
	free(file->key);
	free(file->name);

	if (file->nb_peers > 0) {
		for (int i = 0; i < file->nb_peers; i++) {
			free(file->peers[i]);
		}
		free(file->peers);
	}

	free(file);
}