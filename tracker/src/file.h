#ifndef FILE_H
#define FILE_H

#include "peer.h"

struct file {
	char *name;
	char *key;
	int filesize;
	int piecesize;
	int nb_peers;
	struct peer *peers;
};

/* Add a file to the list as a seeder (so all informations of
 * the file is required). If the file is already in the list, the
 * seeder is added in the list of peers owning the file. Check if
 * all informations are correct.
 */
int add_seed(char *name, int filesize, int piecesize, char *key,
    struct file files[], int *size, struct peer *peer);

/* Add a file to the list as a leecher (so only the key is required).
 * Check if the key exists.
 */
int add_leech(char *key, struct file files[], int *size, struct peer *peer);

// Remove a file in file_list
// Doesn't check if file already in file_list
int rm_file(struct file file, struct file files[], int *size);

/* Search a file in the list of files from its key and return a
 * pointer if its found, else NULL.
 */
struct file *seek_filename(char *key, struct file files[], int *size);

#endif