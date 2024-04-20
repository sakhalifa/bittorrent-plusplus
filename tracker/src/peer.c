#include <stdlib.h>
#include <string.h>
#include "peer.h"


struct peer * create_peer(char * ip, int port) {
    struct peer * p = malloc(sizeof(struct peer));
    
    p->ip = malloc(sizeof(char) * (strlen(ip) + 1));
    p->ip = strcpy(p->ip, ip);

    p->port = port;

    return p;
}


void free_peer(struct peer * p) {
    free(p->ip);
    free(p);
}