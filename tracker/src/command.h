#ifndef COMMAND_H
#define COMMAND_H

#include "file.h"

enum comparator { LT = -1, EQ = 0, GT = 1 };

struct criteria {
	char *element; // Name of the criteria
	enum comparator comp; // Comparator
	char *value; // value to compare to
};

enum command_num {
	ANNOUNCE,
	LOOK,
	GETFILE,
	UPDATE,
};

struct command {
	enum command_num command_name;
	void *command_arg;
};

struct announce {
	int port;
	int nb_file;
	struct file *file_list;
	int nb_key;
	char **key_list;
};

struct look {
	int nb_criteria;
	struct criteria **criteria;
};

struct getfile {
	char *key;
};

struct update {
	int nb_key;
	char **key_list;
};

/* ANNOUNCE COMMAND :
 * used by peer when connecting to the tracker for the first time.
 * the tracker get the peer's port and can update its database whit provided
 * pieces of informations (seeded and leeched files by the peer).
 */
char *announce(
    struct announce arg, struct file files[], int *nb_file, struct peer *peer);

/* LOOK COMMAND :
 * used by the peer to retrieve files with specifics criterias.
 */
char *look(
    struct look arg, struct file files[], int *nb_file, struct peer *peer);

/* GETFILE COMMAND :
 * used by the peer to get all peers that own (fully or partially) a file
 * specified with a key.
 */
char *getfile(
    struct getfile arg, struct file files[], int *nb_file, struct peer *peer);

/* UPDATE COMMAND :
 * allow the peer to inform the tracker which new files he currently owns 
 * and leeches.
 * the tracker update its database accordingly.
*/
char *update(
    struct update arg, struct file files[], int *nb_file, struct peer *peer);

#endif