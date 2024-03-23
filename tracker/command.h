#ifndef COMMAND_H
#define COMMAND_H

#include "file.h"

void announce_listen(int port , struct file ** files, int size_file, char ** list_key, int size_keys);

void look();

void getfile(char * key);

void update(char ** list_keys, int size);

#endif