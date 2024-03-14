/***
 * Linked list of peers.
 */
struct peer_list
{
    struct peer *current;
    struct peer_list *next;
};

struct peer
{
    char *ip;
    unsigned short port;
};