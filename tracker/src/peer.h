#ifndef PEER_H
#define PEER_H

/***
 * Linked list of peers.
 */
struct peer_list {
	struct peer *current;
	struct peer_list *next;
};

struct peer {
	char *ip;
	unsigned short port;
};


struct peer * create_peer(char * ip, int port);

void free_peer(struct peer * p);

#endif