#ifndef COMMAND_H
#define COMMAND_H

#include <stdlib.h>
#include <stdio.h>


void announce_listen(int port /*, struct file files[], char * keys[]*/);

void look();

void getfile(char * key);

void update();

#endif