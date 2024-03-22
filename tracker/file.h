#ifndef FILE_H
#define FILE_H

#include "peer.h"


struct file {
    char * name;
    int filesize;
    int piecesize;
    char * key;
    struct peer_list;
};


#endif