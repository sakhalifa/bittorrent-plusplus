#ifndef FILE_H
#define FILE_H

#include "peer.h"

struct file {
	char *name;
	char *key;
	long long int filesize;
	long long int piecesize;
	int nb_peers;
	struct peer **peers;
};

enum comparator { LT = 0, EQ = 1, GT = 2 };

struct criteria {
	char *element; // Name of the criteria
	enum comparator comp; // Comparator
	char *value; // value to compare to
};

/* Add a file to the list as a seeder (so all informations of
 * the file is required). If the file is already in the list, the
 * seeder is added in the list of peers owning the file. Check if
 * all informations are correct.
 */
struct file **add_seed(
    struct file *f, struct file **files, int *size, struct peer *peer);

/* Add a file to the list as a leecher (so only the key is required).
 * Check if the key exists.
 */
struct file **add_leech(
    char *key, struct file **files, int *size, struct peer *peer);

/* Search a file in the list of files from its key and return a
 * pointer if its found, else NULL.
 */
struct file *seek_filename(char *key, struct file **files, int *size);

/* Dynamically allocate a struct file
 */
struct file *create_file(char *name, long long int filesize, int piecesize, char *key);

/* Free a struct file (and its attribute)
 */
void free_file(struct file *file);

/* Add to res all files that verified the criteria crit in the file list
 * files
 */
struct file **look_criteria(struct criteria *crit, struct file **files,
    int *size, struct file **res, int *size_res);

/* Return a file as a string of Name Filesize Piecesize Key
 */
char *file_to_string(struct file *f);

/* Return a boolean corresponding to if the condition of the criteria is
 * verified (or -1 in case of error, unknown cases)
 */
int check_criteria(struct criteria *crit, struct file *f);

void free_criteria(struct criteria * crit);

#endif