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
	struct file **file_list;
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

void announce(struct command command, struct file files[], struct peer peer);

void look(struct command command, struct file files[], struct peer peer);

void getfile(struct command command, struct file files[], struct peer peer);

void update(struct command command, struct file files[], struct peer peer);

#endif