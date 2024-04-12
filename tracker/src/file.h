#ifndef FILE_H
#define FILE_H

#include "peer.h"

struct file {
	char *name;
	int filesize;
	int piecesize;
	char *key;
	struct peer_list peers;
};

struct file * file_list[];
int size = 0;


// Add a file in file_list
// Doesn't check if file already in file_list
int add_file(struct file file);

// Add a file in file_list
// Doesn't check if file already in file_list
int rm_file(struct file file);

struct file * seek_filename(char * filename);



#endif