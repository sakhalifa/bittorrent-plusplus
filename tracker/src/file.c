#include "file.h"
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct file **add_seed(
    struct file *f, struct file **files, int *size, struct peer *peer) {
	printf("\npizza1\n");
	struct file *file = seek_filename(f->key, files, size);
	printf("\npizza2\n");
	if (file == NULL) {
		*size += 1;
		files = realloc(files, sizeof(struct file *) * (*size));
		files[*size - 1] =
		    create_file(f->name, f->filesize, f->piecesize, f->key);
		files[*size - 1]->peers    = malloc(sizeof(struct peer *));
		files[*size - 1]->peers[0] = create_peer(peer->ip, peer->port);
		files[*size - 1]->nb_peers = 1;
		return files;
	}

	// check if file already existing has same information
	if (strcmp(file->name, f->name) != 0 || file->filesize != f->filesize ||
	    file->piecesize != f->piecesize) {
		return NULL;
	}

	add_leech(f->key, files, size, peer);
	return files;
}

struct file **add_leech(
    char *key, struct file **files, int *size, struct peer *peer) {
	struct file *file = seek_filename(key, files, size);
	if (file == NULL) {
		return NULL; // File not found
	}

	for (int i = 0; i < file->nb_peers; i++) {
		if (peer->port == file->peers[i]->port && strcmp(peer->ip, file->peers[i]->ip) == 0) {
			return NULL;
		}
	}

	file->nb_peers++;
	file->peers = realloc(file->peers, sizeof(struct peer *) * file->nb_peers);
	file->peers[file->nb_peers - 1] = create_peer(peer->ip, peer->port);
	return files;
}

struct file *seek_filename(char *key, struct file **files, int *size) {
	for (unsigned int i = 0; i < *size; i++) {
		if (strcmp(files[i]->key, key) == 0) {
			return files[i];
		}
	}
	return NULL;
}

struct file *create_file(
    char *name, long long int filesize, int piecesize, char *key) {
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
			free_peer(file->peers[i]);
		}
		free(file->peers);
	}

	free(file);
}

int isnumber(char *str) {
	int length = strlen(str);
	for (int i = 0; i < length; i++) {
		if (!(isdigit(str[i]))) {
			return -1;
		}
	}
	return 1;
}

int check_criteria(struct criteria *crit, struct file *f) {
	// Check comparator is not unknown
	if (crit->comp == -1) {
		return 0;
	}
	
	// Check filename
	if (strcmp(crit->element, "filename") == 0 && crit->comp == EQ) {
		return strcmp(crit->value, f->name) == 0;
	}

	// Check key
	if (strcmp(crit->element, "key") == 0 && crit->comp == EQ) {
		return strcmp(crit->value, f->key) == 0;
	}

	// Check filesize
	if (strcmp(crit->element, "filesize") == 0) {
		if (!(isnumber(crit->value)) || strcmp(crit->value, "") == 0) {
			return -1;
		}
		switch (crit->comp) {
		case EQ: return (atoll(crit->value) == f->filesize); break;
		case LT: return (atoll(crit->value) > f->filesize); break;
		case GT: return (atoll(crit->value) < f->filesize); break;
		default: return -1; break;
		}
	}

	// Check piecesize
	if (strcmp(crit->element, "piecesize") == 0) {
		if (!(isnumber(crit->value)) || strcmp(crit->value, "") == 0) {
			return -1;
		}
		switch (crit->comp) {
		case EQ: return (atoll(crit->value) == f->piecesize); break;
		case LT: return (atoll(crit->value) > f->piecesize); break;
		case GT: return (atoll(crit->value) < f->piecesize); break;
		default: return -1; break;
		}
	}

	return -1;
}

char *file_to_string(struct file *f) {
	int size = strlen(f->key) + strlen(f->name) +
	    snprintf(NULL, 0, "%lld", f->filesize) +
	    snprintf(NULL, 0, "%lld", f->piecesize) + 4;
	char *string = malloc(sizeof(char) * size);
	strcpy(string, f->name);
	strcat(string, " ");
	char filesize_length = snprintf(NULL, 0, "%lld", f->filesize) + 1;
	char *filesize       = malloc(sizeof(char) * filesize_length);
	snprintf(filesize, filesize_length, "%lld", f->filesize);
	strcat(string, filesize);
	free(filesize);
	strcat(string, " ");
	char piecesize_length = snprintf(NULL, 0, "%lld", f->piecesize) + 1;
	char *piecesize       = malloc(sizeof(char) * piecesize_length);
	snprintf(piecesize, piecesize_length, "%lld", f->piecesize);
	strcat(string, piecesize);
	free(piecesize);
	strcat(string, " ");
	strcat(string, f->key);
	return string;
}

struct file **look_criteria(struct criteria *crit, struct file **files,
    int *size, struct file **res, int *size_res) {
	for (int i = 0; i < *size; i++) {
		if (check_criteria(crit, files[i]) == 1) {
			if (*size_res == 0) {
				res = malloc(sizeof(struct file *));
			} else {
				res = realloc(res, sizeof(struct file *) * (*size + 1));
			}
			res[*size_res] = create_file(files[i]->name, files[i]->filesize,
			    files[i]->piecesize, files[i]->key);
			*size_res += 1;
		}
	}
	return res;
}

void free_criteria(struct criteria *crit) {
	free(crit->element);
	free(crit->value);
	free(crit);
}

struct file **deep_copy(struct file **files, int *size) {
	struct file **new = malloc(sizeof(struct file *) * (*size));
	for (int i = 0; i < *size; i++) {
		new[i] = create_file(files[i]->name, files[i]->filesize,
		    files[0]->piecesize, files[0]->key);
	}
	return new;
}
