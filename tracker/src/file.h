#ifndef FILE_H
#define FILE_H

#include "peer.h"

struct file {
	char *name;
	char *key;
	int filesize;
	int piecesize;
	int nb_peers;
	struct peer **peers;
};

/* Add a file to the list as a seeder (so all informations of
 * the file is required). If the file is already in the list, the
 * seeder is added in the list of peers owning the file. Check if
 * all informations are correct.
 */
int add_seed(struct file *f, struct file **files, int *size, struct peer *peer);

/* Add a file to the list as a leecher (so only the key is required).
 * Check if the key exists.
 */
int add_leech(char *key, struct file **files, int *size, struct peer *peer);

/* Search a file in the list of files from its key and return a
 * pointer if its found, else NULL.
 */
struct file *seek_filename(char *key, struct file **files, int *size);

/* Dynamically allocate a struct file
 */
struct file *create_file(char *name, int filesize, int piecesize, char *key);

/* Free a struct file (and its attribute)
 */
void free_file(struct file *file);

#endif